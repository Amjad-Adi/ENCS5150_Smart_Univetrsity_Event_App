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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminEventController;
import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminEventsFragment extends Fragment {

    private AdminEventController controller;
    private RecyclerView rvAdminEvents;
    private TextInputEditText etSearch;
    private TextView tvNoData;
    private MaterialButton btnFilter;
    private AutoCompleteTextView autoCompleteCategory;
    private ExtendedFloatingActionButton fabAddEvent;

    private EventAdapter adapter;
    private String currentSearchBy = EventContract.COLUMN_TITLE; // Default column target
    private boolean isAscending = true;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final int SEARCH_DELAY_MS = 300;

    public AdminEventsFragment() {}

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
                    .inflate(R.layout.item_admin_events, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            EventSummary summary = items.get(position);
            holder.bind(summary);
            holder.itemView.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    EventSummary currentSummary = items.get(adapterPosition);
                    AdminEventDetailsBottomSheet bottomSheet = new AdminEventDetailsBottomSheet();
                    bottomSheet.setSetupData(currentSummary, ((AdminActivity) requireActivity()).getAdminEventDetailsController());
                    bottomSheet.show(getParentFragmentManager(), "EventDetailSheet");
                }
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class EventViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivEventImage;
            private final TextView tvTitle, tvCategory, tvDate, tvLocation, tvCapacity, tvFavoriteCount, tvEventStatus;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                ivEventImage = itemView.findViewById(R.id.ivEventImage);
                tvTitle = itemView.findViewById(R.id.tvEventTitle);
                tvCategory = itemView.findViewById(R.id.tvEventCategory);
                tvDate = itemView.findViewById(R.id.tvEventDate);
                tvLocation = itemView.findViewById(R.id.tvEventLocation);
                tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
                tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);
                tvEventStatus = itemView.findViewById(R.id.tvEventStatus);
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
                if (summary.favoriteCount() != 0) {
                    tvFavoriteCount.setText(String.valueOf(summary.favoriteCount()));
                }
                if (summary.isEnabled()) {
                    tvEventStatus.setText("ENABLED");
                    tvEventStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
                } else {
                    tvEventStatus.setText("DISABLED");
                    tvEventStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.warning));
                }
                if (event.getImagePath() == null || event.getImagePath().isEmpty()) {
                    ivEventImage.setImageResource(R.drawable.events);
                } else {
                    Glide.with(itemView.getContext())
                            .load(event.getImagePath())
                            .skipMemoryCache(true)
                            .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.events)
                            .error(R.drawable.events)
                            .into(ivEventImage);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof AdminActivity) {
            controller = ((AdminActivity) getActivity()).getAdminEventController();
        }
        rvAdminEvents = view.findViewById(R.id.rvAdminEvents);
        etSearch = view.findViewById(R.id.etSearchEvents);
        btnFilter = view.findViewById(R.id.btnFilterEvents);
        tvNoData = view.findViewById(R.id.emptyView);
        autoCompleteCategory = view.findViewById(R.id.autoComplete_Category);
        fabAddEvent = view.findViewById(R.id.fabAddEvent);
        setupSearch();
        setupCategoryDropdown();
        setupSortButton();
        adapter = new EventAdapter();
        rvAdminEvents.setAdapter(adapter);
        refreshList();
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminAddEventBottomSheet bottomSheet = new AdminAddEventBottomSheet();
                bottomSheet.setSetupData(((AdminActivity) requireActivity()).getAdminAddEventController());
                bottomSheet.show(getParentFragmentManager(), "AddEventSheet");
            }
        });

        getParentFragmentManager().setFragmentResultListener("RefreshEventList", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                if (bundle.getBoolean("isUpdated", false)) {
                    refreshList();
                }
            }
        });
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
                adapter.updateData(controller.toggleSortDirection(isAscending));
            }
        });
    }

    private void updateSortIcon() {
        int iconRes = isAscending ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        btnFilter.setIconResource(iconRes);
    }

    private void refreshList() {
        String query = etSearch.getText() != null ? etSearch.getText().toString() : "";
        List<EventSummary> results = controller.performSearch(currentSearchBy, isAscending, query);
        if (results.isEmpty()) {
            rvAdminEvents.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvAdminEvents.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            adapter.updateData(results);
        }
        updateSortIcon();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}