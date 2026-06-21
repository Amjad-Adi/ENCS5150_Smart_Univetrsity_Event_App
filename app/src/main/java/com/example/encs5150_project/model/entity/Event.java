package com.example.encs5150_project.model.entity;

import java.time.*;

public class Event {
    private long id;
    private String description;
    private String title;
    private String category;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private int totalSeats;
    private String imagePath;
    private EntityStatus status;
    public Event( String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats,EntityStatus status) {
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.status=status;
    }
    public Event(long id, String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats,EntityStatus status) {
        this.id = id;
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.status=status;
    }
    public Event( String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats,String imagePath,EntityStatus status) {
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.imagePath=imagePath;
        this.status=status;
    }
    public Event(long id, String title,String description, String category, LocalDate date, LocalTime time, String location, int totalSeats,String imagePath,EntityStatus status) {
        this.id = id;
        this.title = title;
        this.description=description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.totalSeats = totalSeats;
        this.imagePath=imagePath;
        this.status=status;
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if(totalSeats<=0)
            throw new IllegalArgumentException("Total seats must be positive");
        this.totalSeats = totalSeats;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }
}
