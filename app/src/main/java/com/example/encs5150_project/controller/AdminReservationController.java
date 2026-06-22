package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.repository.ReservationRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminReservationController {
    private final ReservationRepository reservationRepository;

    private List<Reservation> ascendingList = new ArrayList<>();
    private List<Reservation> descendingList = new ArrayList<>();
    public AdminReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> performSearch(String filterBy, boolean isAscending, String query) {
        reservationRepository.autoUpdateCompletedReservations();
        String cleanQuery = (query == null) ? "" : query.trim();
        ascendingList = reservationRepository.search(filterBy, true, cleanQuery);
        descendingList = reservationRepository.search(filterBy, false, cleanQuery);
        return isAscending ? ascendingList : descendingList;
    }
    public List<Reservation> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }
}