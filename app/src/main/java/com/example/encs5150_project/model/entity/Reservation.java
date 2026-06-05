package com.example.encs5150_project.model.entity;

import java.time.OffsetDateTime;

public class Reservation {
    private long id;
    private User user;
    private Event event;
    private  ReservationType reservationType;
    private int participationCount;
    private ReservationStatus reservationStatus;
    private String reservationAdditionalInfo;
    private final OffsetDateTime reservationDate;
    private Review review;
    public Reservation() {
        reservationDate=OffsetDateTime.now();
    }
    public Reservation(User user, Event event, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo,Review review) {
        this.user = user;
        this.event = event;
        this.reservationType = reservationType;
        this.participationCount = participationCount;
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = OffsetDateTime.now();
        this.review=review;
        if(!user.addReservation(this))
                throw new IllegalArgumentException("The user with id: "+user.getId()+" has been added before");
        event.addReservation(this);
    }
    public Reservation(long id,User user, Event event, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo,Review review) {
        this.id=id;
        this.user = user;
        this.event = event;
        this.reservationType = reservationType;
        this.participationCount = participationCount;
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = OffsetDateTime.now();
        this.review=review;
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

    public OffsetDateTime getReservationDate() {
        return reservationDate;
    }

    public String getReservationAdditionalInfo() {
        return reservationAdditionalInfo;
    }

    public void setReservationAdditionalInfo(String reservationAdditionalInfo) {
        this.reservationAdditionalInfo = reservationAdditionalInfo;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
