package com.example.encs5150_project.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Favourite {
    private final long userId;
    private final long eventId;
    private final OffsetDateTime favoriteDate;
    public Favourite(long userId, long eventId,OffsetDateTime favoriteDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.favoriteDate = favoriteDate;
    }

    public long getUserId() {
        return userId;
    }


    public long getEventId() {
        return eventId;
    }


    public OffsetDateTime getFavoriteDate() {
        return favoriteDate;
    }
}
