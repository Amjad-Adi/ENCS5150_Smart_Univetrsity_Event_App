package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AdminAddEventController {
    private final static int maxAttempts=3;
    private final EventRepository eventRepository;
    public enum AddStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }
    public record AddResponse(AddStatus status, String message) {}

    public AdminAddEventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public AddResponse addEvent(String title, String description, String category, int totalSeats, String location, LocalDate date, LocalTime time, String imagePath) {
        if (title == null || title.trim().isEmpty())
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Event title is required");
        if (location == null || location.trim().isEmpty())
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Location is required");
        if (date == null)
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Please select a valid date");
        if (time == null)
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Please select a valid time");
        if (totalSeats <= 0)
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Total seats must be a positive number greater than 0");
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                long generatedLocalId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
                Event newEvent = new Event(generatedLocalId, title.trim(), description != null ? description.trim() : "", category != null ? category.trim() : "", date, time, location.trim(), totalSeats, EntityStatus.ENABLED);

                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    newEvent.setImagePath(imagePath.trim());
                }
                eventRepository.insert(newEvent);
                return new AddResponse(AddStatus.SUCCESS, "Event created successfully");
            } catch (android.database.sqlite.SQLiteConstraintException e) {
                attempts++;
                if (attempts == maxAttempts) {
                    e.printStackTrace();
                    return new AddResponse(AddStatus.ERROR_SYSTEM, "System error: Failed to generate a unique Event ID after multiple attempts.");
                }
            } catch (IllegalArgumentException e) {
                return new AddResponse(AddStatus.ERROR_VALIDATION, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new AddResponse(AddStatus.ERROR_SYSTEM, "System error occurred while creating the event");
            }
        }
        return new AddResponse(AddStatus.ERROR_SYSTEM, "Unexpected error occurred.");
    }
}