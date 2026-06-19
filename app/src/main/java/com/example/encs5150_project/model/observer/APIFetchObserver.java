package com.example.encs5150_project.model.observer;

import com.example.encs5150_project.model.entity.Event;

import java.util.List;

public interface APIFetchObserver {
    void onFetchSuccess(List<Event> eventList);
    void onFetchFailure(int error);
}
