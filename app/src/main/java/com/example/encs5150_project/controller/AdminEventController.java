package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminEventController {

    private final EventRepository eventRepository;
    private List<EventSummary> ascendingList = new ArrayList<>();
    private List<EventSummary> descendingList = new ArrayList<>();

    public AdminEventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    public List<EventSummary> performSearch(String filterBy, boolean isAscending, String query) {
        String cleanQuery = (query == null) ? "" : query.trim();
        ascendingList = eventRepository.searchEventSummariesForAdmin(filterBy, true, cleanQuery);
        descendingList = eventRepository.searchEventSummariesForAdmin(filterBy, false, cleanQuery);
        return isAscending ? ascendingList : descendingList;
    }

    public List<EventSummary> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }
}