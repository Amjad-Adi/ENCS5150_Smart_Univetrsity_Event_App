package com.example.encs5150_project.model.entity;

import java.time.OffsetDateTime;

public class Reservation {
    private long id;
    private final long userId;
    private final long eventId;
    private  ReservationType reservationType;
    private int participationCount;
    private ReservationStatus reservationStatus;
    private String reservationAdditionalInfo;
    private final OffsetDateTime reservationDate;
    public Reservation(long userId, long eventId, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo,OffsetDateTime reservationDate) {
        this.userId = userId;
        this.eventId = eventId;
        this.reservationType = reservationType;
        setParticipationCount(participationCount);
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = reservationDate;
    }
    public Reservation(long id,long userId, long eventId, ReservationType reservationType, int participationCount, ReservationStatus reservationStatus, String reservationAdditionalInfo,OffsetDateTime reservationDate) {
        this.id=id;
        this.userId = userId;
        this.eventId = eventId;
        this.reservationType = reservationType;
        setParticipationCount(participationCount);
        this.reservationStatus = reservationStatus;
        this.reservationAdditionalInfo = reservationAdditionalInfo;
        this.reservationDate = reservationDate;
    }
    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public long getUserId() {
        return userId;
    }

    public long getEventId() {
        return eventId;
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
        if (participationCount<=0)
            throw new IllegalArgumentException("Participation count must be positive");
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
}
