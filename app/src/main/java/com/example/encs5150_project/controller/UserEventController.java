package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class UserEventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;
    private List<EventSummary> ascendingList = new ArrayList<>();
    private List<EventSummary> descendingList = new ArrayList<>();
    public UserEventController(EventRepository eventRepository,UserRepository userRepository, SharedPrefManager sharedPrefManager) {
        this.eventRepository = eventRepository;
        this.userRepository=userRepository;
        this.sharedPrefManager=sharedPrefManager;
    }

    public List<EventSummary> performSearch(String filterBy, boolean isAscending, String query) {
        String cleanQuery = (query == null) ? "" : query.trim();
        User user=userRepository.findByEmail( sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL,PreferencesConstants.DEFAULT_SESSION_EMAIL));
        ascendingList = eventRepository.searchEventSummariesForUser(filterBy, true, cleanQuery,user);
        descendingList = eventRepository.searchEventSummariesForUser(filterBy, false, cleanQuery,user);
        return isAscending ? ascendingList : descendingList;
    }

    public List<EventSummary> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }
    public User getUser(){
        return userRepository.findByEmail(sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL,PreferencesConstants.DEFAULT_SESSION_EMAIL));
    }
}