package com.example.encs5150_project.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.UserHomeController;
import com.example.encs5150_project.model.UserReservationSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.entity.ReservationStatus;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserHomeFragment extends Fragment {

    private UserHomeController controller;

    private TextView tvWelcomeUser, tvViewAllReservations, tvViewAllHighDemand;
    private RecyclerView rvMyReservations, rvHighDemandEvents;
    private BarChart userCategoryBarChart;

    public UserHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);
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
        if (getActivity() instanceof UserActivity) {
            controller = ((UserActivity) getActivity()).getUserHomeController();
        }
        initViews(view);
        if (controller != null) {
            loadDashboardData();
        }
    }

    private void initViews(View view) {
        tvWelcomeUser = view.findViewById(R.id.tvWelcomeUser);
        tvViewAllReservations = view.findViewById(R.id.tvViewAllReservations);
        tvViewAllHighDemand = view.findViewById(R.id.tvViewAllHighDemand);
        rvMyReservations = view.findViewById(R.id.rvMyReservations);
        rvHighDemandEvents = view.findViewById(R.id.rvHighDemandEvents);
        userCategoryBarChart = view.findViewById(R.id.userCategoryBarChart);
    }

    private void loadDashboardData() {
        User currentUser = controller.getCurrentUser();
        if (currentUser != null) {
            tvWelcomeUser.setText("Welcome, " + currentUser.getFirstName() + " \uD83D\uDC4B");
        }
        setupRecentReservations(controller.getRecentReservations());
        setupHighDemandEvents(controller.getHighDemandEvents());
        setupCategoryBarChart(controller.getUserCategoryStats());
        setupClickListeners();
    }

    private void setupRecentReservations(List<UserReservationSummary> summaries) {
        if (summaries == null || summaries.isEmpty()) return;
        RecentReservationAdapter adapter = new RecentReservationAdapter(summaries);
        rvMyReservations.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyReservations.setAdapter(adapter);
    }

    private void setupHighDemandEvents(List<Event> events) {
        if (events == null || events.isEmpty()) return;
        HighDemandEventAdapter adapter = new HighDemandEventAdapter(events);
        rvHighDemandEvents.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHighDemandEvents.setAdapter(adapter);
    }

    private void setupCategoryBarChart(Map<String, Integer> categoryStats) {
        if (categoryStats == null || categoryStats.isEmpty()) {
            userCategoryBarChart.setVisibility(View.GONE);
            return;
        }
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryStats.entrySet());
        Collections.sort(sortedEntries, (a, b) -> b.getValue().compareTo(a.getValue()));
        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Events Attended");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });
        BarData barData = new BarData(dataSet);
        userCategoryBarChart.setData(barData);
        XAxis xAxis = userCategoryBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        userCategoryBarChart.getDescription().setEnabled(false);
        userCategoryBarChart.getLegend().setEnabled(false);
        userCategoryBarChart.getAxisRight().setEnabled(false);
        userCategoryBarChart.getAxisLeft().setAxisMinimum(0f);
        userCategoryBarChart.getAxisLeft().setGranularity(1f);
        userCategoryBarChart.getAxisLeft().setValueFormatter(
                new com.github.mikephil.charting.formatter.ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });
        userCategoryBarChart.animateY(1000);
        userCategoryBarChart.invalidate();
    }

    private void setupClickListeners() {
        tvViewAllReservations.setOnClickListener(v -> {
            if (getActivity() instanceof UserActivity) {
                ((UserActivity) getActivity()).navigateToReservations();
            }
        });

        tvViewAllHighDemand.setOnClickListener(v -> {
            if (getActivity() instanceof UserActivity) {
                ((UserActivity) getActivity()).navigateToEvents();
            }
        });
    }

    private class RecentReservationAdapter
            extends RecyclerView.Adapter<RecentReservationAdapter.ViewHolder> {

        private final List<UserReservationSummary> items;

        RecentReservationAdapter(List<UserReservationSummary> items) {
            this.items = new ArrayList<>(items);
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
            private final TextView tvResStatus, tvResId, tvResType,
                    tvResEventName, tvResDetails, tvResTimestamp, tvResAdditionalInfo;
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvResStatus= itemView.findViewById(R.id.tvResStatus);
                tvResId  = itemView.findViewById(R.id.tvResId);
                tvResType = itemView.findViewById(R.id.tvResType);
                tvResEventName = itemView.findViewById(R.id.tvResEventName);
                tvResDetails= itemView.findViewById(R.id.tvResDetails);
                tvResTimestamp = itemView.findViewById(R.id.tvResTimestamp);
                tvResAdditionalInfo= itemView.findViewById(R.id.tvResAdditionalInfo);
            }

            void bind(UserReservationSummary summary) {
                Reservation reservation = summary.reservation();
                tvResId.setText("RES-" + reservation.getId());
                tvResType.setText("• " + reservation.getReservationType().name());
                tvResEventName.setText(summary.eventTitle());
                tvResDetails.setText(reservation.getParticipationCount() + " Seats");
                ReservationStatus status = reservation.getReservationStatus();
                tvResStatus.setText(status.name().replace("_", " "));
                switch (status) {
                    case CONFIRMED:
                        tvResStatus.setTextColor(
                                ContextCompat.getColor(itemView.getContext(), R.color.success));
                        break;
                    case DELETED_BY_USER:
                        tvResStatus.setTextColor(
                                ContextCompat.getColor(itemView.getContext(), R.color.warning));
                        break;
                    case COMPLETED:
                        tvResStatus.setTextColor(
                                ContextCompat.getColor(itemView.getContext(), R.color.colorSecondary));
                        break;
                }
                DateTimeFormatter uiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
                String formattedDate = reservation.getReservationDate() != null ? reservation.getReservationDate().format(uiFormatter) : "N/A";
                tvResTimestamp.setText("Reserved: " + formattedDate);
                String info = reservation.getReservationAdditionalInfo();
                if (info == null || info.trim().isEmpty()
                        || info.equals(ReservationContract.DEFAULT_ADDITIONAL_INFO)) {
                    tvResAdditionalInfo.setText("Note: None");
                } else {
                    tvResAdditionalInfo.setText("Note: " + info);
                }
            }
        }
    }
    private class HighDemandEventAdapter
            extends RecyclerView.Adapter<HighDemandEventAdapter.ViewHolder> {
        private final List<Event> items;

        HighDemandEventAdapter(List<Event> items) {
            this.items = new ArrayList<>(items);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_events, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView ivEventImage;
            private final TextView tvTitle, tvCategory, tvDate, tvLocation, tvCapacity;
            private final MaterialButton btnReserveEvent;
            private final ImageView ivEventFavorite;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivEventImage = itemView.findViewById(R.id.ivEventImage);
                ivEventFavorite= itemView.findViewById(R.id.ivEventFavorite);
                tvTitle = itemView.findViewById(R.id.tvEventTitle);
                tvCategory = itemView.findViewById(R.id.tvEventCategory);
                tvDate = itemView.findViewById(R.id.tvEventDate);
                tvLocation = itemView.findViewById(R.id.tvEventLocation);
                tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
                btnReserveEvent= itemView.findViewById(R.id.btnReserveEvent);
            }

            void bind(Event event) {
                tvTitle.setText(event.getTitle());
                tvCategory.setText(event.getCategory());
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
                tvDate.setText(event.getDate().toString() + " • " + event.getTime().format(timeFormatter));
                tvLocation.setText(event.getLocation());
                tvCapacity.setText("(" + event.getTotalSeats() + " Seats)");
                btnReserveEvent.setVisibility(View.GONE);
                ivEventFavorite.setVisibility(View.GONE);
                if (event.getImagePath() == null || event.getImagePath().isEmpty()) {
                    ivEventImage.setImageResource(R.drawable.events);
                } else {
                    Glide.with(itemView.getContext())
                            .load(event.getImagePath())
                            .placeholder(R.drawable.events)
                            .error(R.drawable.events)
                            .into(ivEventImage);
                }
            }
        }
    }
}