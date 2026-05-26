package com.example.encs5150_project.model.entity;

import java.time.LocalDate;

public class Reservation {
    private long id;
    private User user;
    private Event event;
    private  ReservationType reservationType;

    private int participationCount;
    private ReservationStatus reservationStatus;
    private String reservationAdditionalInfo;

    private LocalDate reservationDate;
    private double rating;
    private String review;

    public Reservation() {
    }
    public Reservation(User user, Event event, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo, LocalDate reservationDate, double rating, String review) {
        this.user = user;
        this.event = event;
        this.reservationType = reservationType;
        this.participationCount = participationCount;
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = reservationDate;
        this.rating = rating;
        this.review = review;
        if(!user.addReservation(this))
                throw new IllegalArgumentException("The user with id: "+user.getId()+" has been added before");
        event.addReservation(this);
    }
    public Reservation(long id,User user, Event event, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo, LocalDate reservationDate, double rating, String review) {
        this.id=id;
        this.user = user;
        this.event = event;
        this.reservationType = reservationType;
        this.participationCount = participationCount;
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = reservationDate;
        this.rating = rating;
        this.review = review;
        if(!user.addReservation(this))
            throw new IllegalArgumentException("The user with id: "+user.getId()+" has been added before");
        event.addReservation(this);
    }
    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public int getParticipationCount() {
        return participationCount;
    }

    public void setParticipationCount(int participationCount) {
        this.participationCount = participationCount;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getReservationAdditionalInfo() {
        return reservationAdditionalInfo;
    }

    public void setReservationAdditionalInfo(String reservationAdditionalInfo) {
        this.reservationAdditionalInfo = reservationAdditionalInfo;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
