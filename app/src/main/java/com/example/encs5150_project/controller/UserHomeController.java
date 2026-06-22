package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.UserReservationSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.ReservationRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.util.List;
import java.util.Map;

public class UserHomeController {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final SharedPrefManager sharedPrefManager;
    private static final int RECENT_RESERVATIONS_LIMIT = 3;
    private static final int HIGH_DEMAND_EVENTS_LIMIT = 5;

    public UserHomeController(UserRepository userRepository, EventRepository eventRepository, ReservationRepository reservationRepository, SharedPrefManager sharedPrefManager) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public User getCurrentUser() {
        String email = sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, PreferencesConstants.DEFAULT_SESSION_EMAIL);
        return userRepository.findByEmail(email);
    }

    public List<UserReservationSummary> getRecentReservations() {
        User user = getCurrentUser();
        if (user == null) return null;
        return reservationRepository.getUserRecentReservations(user.getId(), RECENT_RESERVATIONS_LIMIT);
    }

    public List<Event> getHighDemandEvents() {
        return eventRepository.getHighDemandEvents(HIGH_DEMAND_EVENTS_LIMIT);
    }

    public Map<String, Integer> getUserCategoryStats() {
        User user = getCurrentUser();
        if (user == null) return null;
        return reservationRepository.getUserCategoryStats(user.getId());
    }
}