package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.repository.EventRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AdminEventDetailsController {

    private final EventRepository eventRepository;

    public enum DetailStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }

    public record DetailResponse(DetailStatus status, String message) {}

    public AdminEventDetailsController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    public DetailResponse updateEvent(Event event, String title, String description, String category, int totalSeats, String location, LocalDate date, LocalTime time, String imagePath) {
        try {
            if (title == null || title.trim().isEmpty())
                return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Event title is required.");
            if (location == null || location.trim().isEmpty())
                return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Location is required.");
            if (totalSeats <= 0)
                return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Total seats must be a positive number.");
            event.setTitle(title.trim());
            event.setDescription(description != null ? description.trim() : "");
            event.setCategory(category != null ? category.trim() : "");
            event.setTotalSeats(totalSeats);
            event.setLocation(location.trim());
            event.setDate(date);
            event.setTime(time);
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                event.setImagePath(imagePath.trim());
            }
            eventRepository.update(event);
            return new DetailResponse(DetailStatus.SUCCESS, "Event information updated successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while updating the event.");
        }
    }
    public DetailResponse toggleEventStatus(Event event) {
        try {
            if (event.getStatus() == EntityStatus.ENABLED) {
                event.setStatus(EntityStatus.DISABLED);
            } else {
                event.setStatus(EntityStatus.ENABLED);
            }
            eventRepository.changeStatus(event.getId(), event.getStatus());
            String statusMsg = event.getStatus() == EntityStatus.ENABLED ? "Event enabled successfully." : "Event disabled successfully.";
            return new DetailResponse(DetailStatus.SUCCESS, statusMsg);

        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while changing event status.");
        }
    }

}