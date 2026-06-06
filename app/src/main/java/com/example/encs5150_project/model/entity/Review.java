package com.example.encs5150_project.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Review {
    public final long reservationId;
    private double rating;
    private String text;
    private EntityStatus status;
    private final OffsetDateTime reviewDate;
    public Review(long reservationId, double rating, String text,EntityStatus status,OffsetDateTime reviewDate ) {
        this.reservationId = reservationId;
        setRating(rating);
        this.text = text;
        this.reviewDate = reviewDate;
        this.status=status;
    }

    public long getReservationId() {
        return reservationId;
    }


    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if(rating<1||rating>5)
            throw new IllegalArgumentException("Rating should be between 1 and 5");
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public OffsetDateTime getReviewDate() {
        return reviewDate;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }
}
