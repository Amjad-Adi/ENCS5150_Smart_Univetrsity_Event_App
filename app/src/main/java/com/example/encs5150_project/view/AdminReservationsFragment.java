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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminReservationController;
import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminReservationsFragment extends Fragment {

    private AdminReservationController controller;
    private RecyclerView rvAdminReservations;
    private TextInputEditText etSearch;
    private TextView tvNoData;
    private MaterialButton btnFilter;
    private AutoCompleteTextView autoCompleteCategory;

    private ReservationAdapter adapter;
    private String currentSearchBy = ReservationContract.COLUMN_ID; // Default column target
    private boolean isAscending = true;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final int SEARCH_DELAY_MS = 300;

    public AdminReservationsFragment() {}

    private class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
        private final List<Reservation> items = new ArrayList<>();
        public void updateData(List<Reservation> newItems) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            Reservation reservation = items.get(position);
            holder.bind(reservation);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ReservationViewHolder extends RecyclerView.ViewHolder {
            private final TextView tvResStatus, tvResId, tvResType, tvResConnection, tvResDetails, tvResAdditionalInfo;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                tvResStatus = itemView.findViewById(R.id.tvResStatus);
                tvResId = itemView.findViewById(R.id.tvResId);
                tvResType = itemView.findViewById(R.id.tvResType);
                tvResConnection = itemView.findViewById(R.id.tvResConnection);
                tvResDetails = itemView.findViewById(R.id.tvResDetails);
                tvResAdditionalInfo = itemView.findViewById(R.id.tvResAdditionalInfo);
            }

            public void bind(Reservation reservation) {
                tvResStatus.setText(reservation.getReservationStatus().name());
                tvResId.setText("RES-" + reservation.getId());
                tvResType.setText("• " + reservation.getReservationType().name());
                tvResConnection.setText("User ID: #" + reservation.getUserId() + "  →  Event ID: #" + reservation.getEventId());
                tvResDetails.setText(reservation.getReservationDate() + "  |  " + reservation.getParticipationCount() + " Seats");
                String info = reservation.getReservationAdditionalInfo();
                if (info == null || info.trim().isEmpty() || info.equals(ReservationContract.DEFAULT_ADDITIONAL_INFO)) {
                    tvResAdditionalInfo.setText("Note: None");
                } else {
                    tvResAdditionalInfo.setText("Note: " + info);
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof AdminActivity)
            controller = ((AdminActivity) getActivity()).getAdminReservationController();
        rvAdminReservations = view.findViewById(R.id.rvAdminReservations);
        etSearch = view.findViewById(R.id.etSearchReservations);
        btnFilter = view.findViewById(R.id.btnFilterReservations);
        tvNoData = view.findViewById(R.id.emptyView);
        autoCompleteCategory = view.findViewById(R.id.autoComplete_Category);
        setupSearch();
        setupCategoryDropdown();
        setupSortButton();
        adapter = new ReservationAdapter();
        rvAdminReservations.setAdapter(adapter);
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
        String[] displayNames = new String[]{"Reservation ID", "Event ID", "User ID", "Date", "Participation Count", "Type", "Status"};
        String[] columnNames = new String[]{
                ReservationContract.COLUMN_ID,
                ReservationContract.COLUMN_EVENT_ID,
                ReservationContract.COLUMN_USER_ID,
                ReservationContract.COLUMN_DATE,
                ReservationContract.COLUMN_PARTICIPATION_COUNT,
                ReservationContract.COLUMN_TYPE,
                ReservationContract.COLUMN_STATUS
        };

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
        List<Reservation> results = controller.performSearch(currentSearchBy, isAscending, query);

        if (results.isEmpty()) {
            rvAdminReservations.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvAdminReservations.setVisibility(View.VISIBLE);
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