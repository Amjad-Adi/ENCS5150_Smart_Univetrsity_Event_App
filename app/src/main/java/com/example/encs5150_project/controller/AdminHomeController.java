package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.ReservationRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AdminHomeController {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final SharedPrefManager sharedPrefManager;
    private static final int numberOfMonthsInYear = 12;

    public AdminHomeController(AdminRepository adminRepository, UserRepository userRepository, EventRepository eventRepository, ReservationRepository reservationRepository, SharedPrefManager sharedPrefManager) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public Admin getCurrentAdmin() {
        String email = sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, PreferencesConstants.DEFAULT_SESSION_EMAIL);
        return adminRepository.findByEmail(email);
    }

    public int getTotalUsersCount() {
        return userRepository.getTotalUsersCount();
    }

    public int getTotalEventsCount() {
        return eventRepository.getTotalEventsCount();
    }

    public int getTotalAttendeesCount() {
        return reservationRepository.getTotalAttendeesCount();
    }

    public Map<String, Integer> getEventCategoryCounts() {
        return eventRepository.getEventCategoryCounts();
    }

    public Map<Integer, Integer> getDailyReservationsForCurrentMonth() {
        String currentYearMonth = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return reservationRepository.getDailyReservationsForMonth(currentYearMonth);
    }

    public Map<Integer, Integer> getMonthlyParticipationStats() {
        String currentYear = OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy"));
        Map<Integer, Integer> dbStats = reservationRepository.getMonthlyParticipationStats(currentYear);
        Map<Integer, Integer> monthlyStats = new HashMap<>();
        for (int i = 1; i <= numberOfMonthsInYear; i++) {
            monthlyStats.put(i, dbStats.getOrDefault(i, 0));
        }

        return monthlyStats;
    }
}