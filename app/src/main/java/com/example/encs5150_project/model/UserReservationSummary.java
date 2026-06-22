package com.example.encs5150_project.model;

import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.entity.Reservation;

public record UserReservationSummary(Reservation reservation, String eventTitle) {}
