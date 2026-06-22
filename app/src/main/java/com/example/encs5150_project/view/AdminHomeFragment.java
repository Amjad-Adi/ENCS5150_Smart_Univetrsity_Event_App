package com.example.encs5150_project.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminHomeController;
import com.example.encs5150_project.model.entity.Admin;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AdminHomeFragment extends Fragment {

    private AdminHomeController controller;

    private TextView tvWelcome, tvTotalUsersCount, tvTotalEventsCount, tvAttendeesCount;
    private LineChart lineChart;
    private HorizontalBarChart categoryBarChart;
    private BarChart participationBarChart;

    public AdminHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
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
        if (getActivity() instanceof AdminActivity) {
            controller = ((AdminActivity) getActivity()).getAdminHomeController();
        }

        initViews(view);

        if (controller != null) {
            loadDashboardData();
        }
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvTotalUsersCount = view.findViewById(R.id.tvTotalUsersCount);
        tvTotalEventsCount = view.findViewById(R.id.tvTotalEventsCount);
        tvAttendeesCount = view.findViewById(R.id.tvAttendeesCount);
        lineChart = view.findViewById(R.id.lineChart);
        categoryBarChart = view.findViewById(R.id.categoryBarChart);
        participationBarChart = view.findViewById(R.id.participationBarChart);
    }

    private void loadDashboardData() {
        Admin currentAdmin = controller.getCurrentAdmin();
        if (currentAdmin != null) {
            tvWelcome.setText("Welcome, " + currentAdmin.getFirstName() + " \uD83D\uDCCA");
        }
        tvTotalUsersCount.setText(String.valueOf(controller.getTotalUsersCount()));
        tvTotalEventsCount.setText(String.valueOf(controller.getTotalEventsCount()));
        tvAttendeesCount.setText(String.format("%,d", controller.getTotalAttendeesCount()));
        setupLineChart(controller.getDailyReservationsForCurrentMonth());
        setupCategoryBarChart(controller.getEventCategoryCounts());
        setupParticipationBarChart(controller.getMonthlyParticipationStats());
    }
    private void setupLineChart(Map<Integer, Integer> dailyStats) {
        ArrayList<Entry> entries = new ArrayList<>();
        List<Integer> days = new ArrayList<>(dailyStats.keySet());
        Collections.sort(days);
        for (int day : days) {
            entries.add(new Entry(day, dailyStats.get(day)));
        }
        if (entries.isEmpty()) {
            entries.add(new Entry(1, 0));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Reservations (This Month)");
        dataSet.setColor(ColorTemplate.getHoloBlue());
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(ColorTemplate.getHoloBlue());
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.getDescription().setEnabled(false);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(1f);
        xAxis.setAxisMaximum(31f);
        xAxis.setLabelCount(6, false);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        lineChart.getAxisLeft().setGranularity(1f);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        lineChart.getAxisRight().setEnabled(false);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
    private void setupCategoryBarChart(Map<String, Integer> categoryCount) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryCount.entrySet());
        Collections.sort(sortedEntries, (a, b) -> b.getValue().compareTo(a.getValue()));
        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            entries.add(new BarEntry(index, entry.getValue()));
            labels.add(entry.getKey());
            index++;
        }
        BarDataSet dataSet = new BarDataSet(entries, "Events");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });
        BarData barData = new BarData(dataSet);
        categoryBarChart.setData(barData);
        XAxis xAxis = categoryBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        categoryBarChart.getDescription().setEnabled(false);
        categoryBarChart.getLegend().setEnabled(false);
        categoryBarChart.getAxisRight().setEnabled(false);
        categoryBarChart.getAxisLeft().setAxisMinimum(0f);
        categoryBarChart.getAxisLeft().setGranularity(1f);
        categoryBarChart.getAxisLeft().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        categoryBarChart.animateY(1000);
        categoryBarChart.invalidate();
    }

    private void setupParticipationBarChart(Map<Integer, Integer> monthlyStats) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (int i = 1; i <= 12; i++) {
            int count = monthlyStats.containsKey(i) ? monthlyStats.get(i) : 0;
            entries.add(new BarEntry(i - 1, count));
        }
        BarDataSet dataSet = new BarDataSet(entries, "Attendees");
        dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getBarLabel(BarEntry barEntry) {
                return String.valueOf((int) barEntry.getY());
            }
        });
        BarData barData = new BarData(dataSet);
        participationBarChart.setData(barData);
        XAxis xAxis = participationBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(12);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        participationBarChart.getDescription().setEnabled(false);
        participationBarChart.getLegend().setEnabled(false);
        participationBarChart.getAxisRight().setEnabled(false);
        participationBarChart.getAxisLeft().setAxisMinimum(0f);
        participationBarChart.getAxisLeft().setGranularity(1f);
        participationBarChart.getAxisLeft().setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        participationBarChart.animateY(1000);
        participationBarChart.invalidate();
    }
}