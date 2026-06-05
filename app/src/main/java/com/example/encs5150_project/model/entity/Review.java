package com.example.encs5150_project.model.entity;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Review {
    public Reservation reservation;
    private double rating;
    private String text;
    private final OffsetDateTime reviewDate;
    public Review() {
        reviewDate=OffsetDateTime.now();
    }
    public Review(Reservation reservation, double rating, String text) {
        this.reservation = reservation;
        this.rating = rating;
        this.text = text;
        this.reviewDate = OffsetDateTime.now();
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
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
}
