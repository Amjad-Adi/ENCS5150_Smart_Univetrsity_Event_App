package com.example.encs5150_project.model.entity;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Event {
    private long id;
    private String description;
    private String title;
    private String category;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private int totalSeats;
    private URL imagePath;
    private final List<Reservation> reservationList=new ArrayList<>();

    public Event() {
    }
    public Event(String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats, URL imagePath) {
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.imagePath = imagePath;
    }
    public Event(long id, String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats, URL imagePath) {
        this.id = id;
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.imagePath = imagePath;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public URL getImagePath() {
        return imagePath;
    }

    public void setImagePath(URL imagePath) {
        this.imagePath = imagePath;
    }
    public void addReservation(Reservation reservation) {
        reservationList.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservationList.remove(reservation);
    }
    public List<Reservation> getReservationList() {
        return Collections.unmodifiableList(reservationList);
    }

}
