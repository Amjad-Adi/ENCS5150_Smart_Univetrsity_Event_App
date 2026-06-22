package com.example.encs5150_project.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.UserEventController;
import com.example.encs5150_project.controller.UserEventReserveController;
import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserEventsFragment extends Fragment {
    private final static int RESERVATION_DEADLINE_IN_HOURS = 3;
    private UserEventController controller;
    private RecyclerView rvUserEvents;
    private TextInputEditText etSearch;
    private TextView tvNoData;
    private MaterialButton btnFilter;
    private AutoCompleteTextView autoCompleteCategory;

    private EventAdapter adapter;
    private String currentSearchBy = EventContract.COLUMN_TITLE;
    private boolean isAscending = true;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final int SEARCH_DELAY_MS = 300;

    private final int REFRESH_TIME_IN_MS = 60000;

    private final Handler uiRefreshHandler = new Handler(Looper.getMainLooper());
    private final Runnable refreshUiRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null) {
                adapter.notifyItemRangeChanged(0, adapter.getItemCount(), "UPDATE_COUNTS");
            }
            uiRefreshHandler.postDelayed(this, REFRESH_TIME_IN_MS);
        }
    };

    public UserEventsFragment() {}

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
        private final List<EventSummary> items = new ArrayList<>();

        public void updateData(List<EventSummary> newItems) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_events, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            EventSummary summary = items.get(position);
            holder.bind(summary);
            holder.btnReserveEvent.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    EventSummary currentSummary = items.get(adapterPosition);
                    UserEventReserveBottomSheet bottomSheet = new UserEventReserveBottomSheet();
                    UserEventReserveController userEventReserveController = ((UserActivity) getActivity()).getUserEventReserveController();
                    UserEventController userEventController = ((UserActivity) getActivity()).getUserEventController();
                    bottomSheet.setSetupData(currentSummary, userEventReserveController, userEventController.getUser());
                    bottomSheet.show(getParentFragmentManager(), "ReserveEventSheet");
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull List<Object> payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
            } else {
                for (Object payload : payloads) {
                    if (payload instanceof String && payload.equals("UPDATE_COUNTS")) {
                        EventSummary summary = items.get(position);
                        Event event = summary.event();
                        holder.tvCapacity.setText("(" + summary.bookedSeats() + "/" + event.getTotalSeats() + " Seats)");
                        if (summary.bookedSeats() >= event.getTotalSeats() ||
                                LocalDateTime.now().isAfter(LocalDateTime.of(event.getDate(), event.getTime()).minusHours(RESERVATION_DEADLINE_IN_HOURS)) ||
                                event.getStatus() == EntityStatus.DISABLED ||
                                summary.isReserved()) {
                            holder.btnReserveEvent.setVisibility(View.GONE);
                        } else {
                            holder.btnReserveEvent.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        class EventViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivEventImage;
            final TextView tvTitle, tvCategory, tvDate, tvLocation, tvCapacity, tvAverageRating;
            final MaterialButton btnReserveEvent;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                ivEventImage = itemView.findViewById(R.id.ivEventImage);
                tvTitle = itemView.findViewById(R.id.tvEventTitle);
                tvCategory = itemView.findViewById(R.id.tvEventCategory);
                tvDate = itemView.findViewById(R.id.tvEventDate);
                tvLocation = itemView.findViewById(R.id.tvEventLocation);
                tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
                tvAverageRating = itemView.findViewById(R.id.tvAverageRating);
                btnReserveEvent = itemView.findViewById(R.id.btnReserveEvent);
            }

            public void bind(EventSummary summary) {
                Event event = summary.event();
                tvTitle.setText(event.getTitle());
                tvCategory.setText(event.getCategory());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
                String dateTimeFormat = event.getDate().toString() + " • " + event.getTime().format(timeFormatter);
                tvDate.setText(dateTimeFormat);
                tvLocation.setText(event.getLocation());
                tvCapacity.setText("(" + summary.bookedSeats() + "/" + event.getTotalSeats() + " Seats)");
                if (summary.reviewCount() == 0) {
                    tvAverageRating.setText("New");
                } else {
                    tvAverageRating.setText(String.format(Locale.US, "%.1f", summary.averageRating()));
                }
                if (event.getImagePath() == null || event.getImagePath().isEmpty()) {
                    ivEventImage.setImageResource(R.drawable.events);
                } else {
                    Glide.with(itemView.getContext())
                            .load(event.getImagePath())
                            .placeholder(R.drawable.events)
                            .error(R.drawable.events)
                            .into(ivEventImage);
                }
                if (summary.bookedSeats() >= event.getTotalSeats() ||
                        LocalDateTime.now().isAfter(LocalDateTime.of(event.getDate(), event.getTime()).minusHours(RESERVATION_DEADLINE_IN_HOURS)) ||
                        event.getStatus() == EntityStatus.DISABLED ||
                        summary.isReserved()) {
                    btnReserveEvent.setVisibility(View.GONE);
                } else {
                    btnReserveEvent.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof UserActivity) {
            controller = ((UserActivity) getActivity()).getUserEventController();
        }
        rvUserEvents = view.findViewById(R.id.rvUserEvents);
        etSearch = view.findViewById(R.id.etSearchEvents);
        btnFilter = view.findViewById(R.id.btnFilterEvents);
        tvNoData = view.findViewById(R.id.emptyView);
        autoCompleteCategory = view.findViewById(R.id.autoComplete_Category);
        setupSearch();
        setupCategoryDropdown();
        setupSortButton();
        adapter = new EventAdapter();
        rvUserEvents.setAdapter(adapter);
        refreshList();
        getParentFragmentManager().setFragmentResultListener("RefreshUserEventList", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                if (bundle.getBoolean("isUpdated", false)) {
                    refreshList();
                }
            }
        });
        uiRefreshHandler.postDelayed(refreshUiRunnable, REFRESH_TIME_IN_MS);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> refreshList();
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });
    }

    private void setupCategoryDropdown() {
        String[] displayNames = new String[]{"Title", "Description", "Category", "Date", "Time", "Location", "Total Seats", "Status"};
        String[] columnNames = new String[]{EventContract.COLUMN_TITLE, EventContract.COLUMN_DESCRIPTION, EventContract.COLUMN_CATEGORY, EventContract.COLUMN_DATE, EventContract.COLUMN_TIME, EventContract.COLUMN_LOCATION, EventContract.COLUMN_TOTAL_SEATS, EventContract.COLUMN_STATUS};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, displayNames);
        autoCompleteCategory.setAdapter(dropdownAdapter);
        autoCompleteCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSearchBy = columnNames[position];
                refreshList();
            }
        });
    }

    private void setupSortButton() {
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAscending = !isAscending;
                updateSortIcon();
                if (controller != null) {
                    adapter.updateData(controller.toggleSortDirection(isAscending));
                }
            }
        });
    }

    private void updateSortIcon() {
        int iconRes = isAscending ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        btnFilter.setIconResource(iconRes);
    }

    private void refreshList() {
        if (controller == null) return;
        String query = etSearch.getText() != null ? etSearch.getText().toString() : "";
        List<EventSummary> results = controller.performSearch(currentSearchBy, isAscending, query);
        if (results.isEmpty()) {
            rvUserEvents.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvUserEvents.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            adapter.updateData(results);
        }
        updateSortIcon();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacks(searchRunnable);
        uiRefreshHandler.removeCallbacks(refreshUiRunnable);
    }
}