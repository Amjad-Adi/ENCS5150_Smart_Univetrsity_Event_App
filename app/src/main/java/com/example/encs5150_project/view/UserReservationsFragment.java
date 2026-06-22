package com.example.encs5150_project.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.UserReservationsController;
import com.example.encs5150_project.model.UserReservationSummary;
import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.entity.ReservationStatus;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserReservationsFragment extends Fragment {

    private UserReservationsController controller;
    private RecyclerView rvUserReservations;
    private TextInputEditText etSearch;
    private TextView tvNoData;
    private MaterialButton btnFilter;
    private AutoCompleteTextView autoCompleteCategory;

    private UserReservationAdapter adapter;
    private String currentSearchBy = ReservationContract.COLUMN_ID; // Fallback default
    private boolean isAscending = true;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final int SEARCH_DELAY_MS = 300;

    public UserReservationsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_reservation, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof UserActivity)
            controller = ((UserActivity) getActivity()).getUserReservationsController();
        rvUserReservations = view.findViewById(R.id.rvUserReservations);
        etSearch = view.findViewById(R.id.etSearchReservations);
        btnFilter = view.findViewById(R.id.btnFilterReservations);
        tvNoData = view.findViewById(R.id.emptyView);
        autoCompleteCategory = view.findViewById(R.id.autoComplete_Category);
        setupSearch();
        setupCategoryDropdown();
        setupSortButton();
        adapter = new UserReservationAdapter();
        rvUserReservations.setAdapter(adapter);
        refreshList();
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
        String[] displayNames = new String[]{"Reservation ID", "Event Name", "Reservation Date", "Participation Count", "Status"};
        String[] columnNames = new String[]{ReservationContract.COLUMN_ID, EventContract.COLUMN_TITLE, ReservationContract.COLUMN_DATE, ReservationContract.COLUMN_PARTICIPATION_COUNT, ReservationContract.COLUMN_STATUS};
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, displayNames);
        autoCompleteCategory.setAdapter(dropdownAdapter);
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            currentSearchBy = columnNames[position];
            refreshList();
        });
    }

    private void setupSortButton() {
        btnFilter.setOnClickListener(v -> {
            isAscending = !isAscending;
            updateSortIcon();
            if (controller != null) {
                adapter.updateData(controller.toggleSortDirection(isAscending));
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
        List<UserReservationSummary> results = controller.performSearch(currentSearchBy, isAscending, query);

        if (results.isEmpty()) {
            rvUserReservations.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvUserReservations.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            adapter.updateData(results);
        }
        updateSortIcon();
    }

    private class UserReservationAdapter extends RecyclerView.Adapter<UserReservationAdapter.ViewHolder> {
        private final List<UserReservationSummary> items = new ArrayList<>();

        public void updateData(List<UserReservationSummary> newItems) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_reservation, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvResStatus, tvResId, tvResType, tvResEventName, tvResDetails, tvResTimestamp, tvResAdditionalInfo;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvResStatus = itemView.findViewById(R.id.tvResStatus);
                tvResId = itemView.findViewById(R.id.tvResId);
                tvResType = itemView.findViewById(R.id.tvResType);
                tvResEventName = itemView.findViewById(R.id.tvResEventName);
                tvResDetails = itemView.findViewById(R.id.tvResDetails);
                tvResTimestamp = itemView.findViewById(R.id.tvResTimestamp);
                tvResAdditionalInfo = itemView.findViewById(R.id.tvResAdditionalInfo);
            }

            public void bind(UserReservationSummary summary) {
                Reservation reservation = summary.reservation();
                tvResId.setText("RES-" + reservation.getId());
                tvResType.setText("• " + reservation.getReservationType().name());
                tvResEventName.setText(summary.eventTitle());
                tvResDetails.setText(reservation.getParticipationCount() + " Seats");
                ReservationStatus status = reservation.getReservationStatus();
                tvResStatus.setText(status.name().replace("_", " "));
                switch (status) {
                    case CONFIRMED:
                        tvResStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.success));
                        break;
                    case DELETED_BY_USER:
                        tvResStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.warning));
                        break;
                    case COMPLETED:
                        tvStatusTextColorFallback(tvResStatus);
                        break;
                }
                DateTimeFormatter uiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
                String formattedDate = reservation.getReservationDate() != null ? reservation.getReservationDate().format(uiFormatter) : "N/A";
                tvResTimestamp.setText("Reserved: " + formattedDate);
                String info = reservation.getReservationAdditionalInfo();
                if (info == null || info.trim().isEmpty() || info.equals(ReservationContract.DEFAULT_ADDITIONAL_INFO)) {
                    tvResAdditionalInfo.setText("Note: None");
                } else {
                    tvResAdditionalInfo.setText("Note: " + info);
                }
            }

            private void tvStatusTextColorFallback(TextView statusTextView) {
                statusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorSecondary));
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}