package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.entity.ReservationType;
import com.example.encs5150_project.model.repository.ReservationRepository;

public class UserEventReserveController {

    private final ReservationRepository reservationRepository;

    public enum ReservationStatus { SUCCESS, ERROR_VALIDATION, ERROR_SYSTEM }
    public record ReservationResponse(ReservationStatus status, String message) {}

    public UserEventReserveController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
    public ReservationResponse submitReservation(long eventId, long userId, int currentBookedSeats, int totalSeats, int requestedCount, String type, String additionalInfo) {
        if (requestedCount <= 0) {
            return new ReservationResponse(ReservationStatus.ERROR_VALIDATION, "Participation count must be at least 1.");
        }
        int availableSeats = totalSeats - currentBookedSeats;
        int newTotal = currentBookedSeats + requestedCount;
        if (newTotal > totalSeats) {
            return new ReservationResponse(ReservationStatus.ERROR_VALIDATION, "You can only register " + availableSeats + " seats.");
        }
        ReservationType resType;
        try {
            resType = ReservationType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            return new ReservationResponse(ReservationStatus.ERROR_VALIDATION, "Invalid reservation type selected.");
        }
        Reservation reservation = new Reservation(userId, eventId, resType, requestedCount, additionalInfo);
        try {
            reservationRepository.insert(reservation);
            return new ReservationResponse(ReservationStatus.SUCCESS, "Reservation confirmed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return new ReservationResponse(ReservationStatus.ERROR_SYSTEM, "An error occurred while saving your reservation. Please try again.");
        }
    }
}