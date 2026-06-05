package com.example.encs5150_project.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Favourite {
    private User user;
    private Event event;
    private final OffsetDateTime favoriteDate;
    public Favourite() {
        favoriteDate=OffsetDateTime.now();
    }
    public Favourite(User user, Event event) {
        this.user = user;
        this.event = event;
        this.favoriteDate = OffsetDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setPerson(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public OffsetDateTime getFavoriteDate() {
        return favoriteDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Favourite))
            return false;
        return user==((Favourite) o).user&& event==((Favourite) o).event;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(user)*31+System.identityHashCode(event);
    }
}
