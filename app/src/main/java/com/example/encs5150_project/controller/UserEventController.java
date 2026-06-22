package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.Favourite;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.repository.FavouriteRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class UserEventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final FavouriteRepository favouriteRepository;
    private final SharedPrefManager sharedPrefManager;
    private List<EventSummary> ascendingList = new ArrayList<>();
    private List<EventSummary> descendingList = new ArrayList<>();

    public UserEventController(EventRepository eventRepository, UserRepository userRepository, FavouriteRepository favouriteRepository, SharedPrefManager sharedPrefManager) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.favouriteRepository = favouriteRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public List<EventSummary> performSearch(String filterBy, boolean isAscending, String query) {
        String cleanQuery = (query == null) ? "" : query.trim();
        User user = getUser();
        ascendingList = eventRepository.searchEventSummariesForUser(filterBy, true, cleanQuery, user);
        descendingList = eventRepository.searchEventSummariesForUser(filterBy, false, cleanQuery, user);
        return isAscending ? ascendingList : descendingList;
    }

    public List<EventSummary> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }

    public User getUser() {
        return userRepository.findByEmail(sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, PreferencesConstants.DEFAULT_SESSION_EMAIL));
    }

    public boolean isFavorited(long eventId) {
        User user = getUser();
        if (user == null) return false;
        return favouriteRepository.findById(eventId, user.getId()) != null;
    }

    public void toggleFavourite(long eventId, boolean isCurrentlyFavorited) {
        User user = getUser();
        if (user == null) return;
        if (isCurrentlyFavorited) {
            favouriteRepository.delete(user.getId(), eventId);
        } else {
            favouriteRepository.insert(new Favourite(user.getId(), eventId, OffsetDateTime.now(ZoneOffset.UTC)));
        }
    }
}