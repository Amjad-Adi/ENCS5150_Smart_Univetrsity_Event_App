package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.UserReservationSummary;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.ReservationRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class UserReservationsController {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;

    private List<UserReservationSummary> ascendingList = new ArrayList<>();
    private List<UserReservationSummary> descendingList = new ArrayList<>();

    public UserReservationsController(ReservationRepository reservationRepository, UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public User getUser() {
        return userRepository.findByEmail(sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, PreferencesConstants.DEFAULT_SESSION_EMAIL));
    }

    public List<UserReservationSummary> performSearch(String filterBy, boolean isAscending, String query) {
        User user = getUser();
        if (user == null) return new ArrayList<>();
        String cleanQuery = (query == null) ? "" : query.trim();
        ascendingList = reservationRepository.searchUserReservations(user.getId(), filterBy, true, cleanQuery);
        descendingList = reservationRepository.searchUserReservations(user.getId(), filterBy, false, cleanQuery);
        return isAscending ? ascendingList : descendingList;
    }

    public List<UserReservationSummary> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }
}