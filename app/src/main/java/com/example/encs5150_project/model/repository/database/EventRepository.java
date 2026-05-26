package com.example.encs5150_project.model.repository.database;

import com.example.encs5150_project.model.entity.Event;

import java.util.Collections;
import java.util.List;

public class EventRepository implements DataBaseRepository<Event>{

    @Override
    public void save(Event entity) {

    }

    @Override
    public void update(Event entity) {

    }

    @Override
    public Event findById(long id) {
        return null;
    }

    @Override
    public List<Event> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
