package com.example.encs5150_project.model;
import com.example.encs5150_project.model.entity.Event;

public record EventSummary(
        Event event,
        int bookedSeats,
        double averageRating,
        int reviewCount
) {}