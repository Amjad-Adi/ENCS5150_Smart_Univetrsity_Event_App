package com.example.encs5150_project.model.entity;

import java.time.LocalDate;

public class Favourite {
    private Person person;
    private Event event;
    private LocalDate favoriteDate;

    public Favourite(Person person, Event event, LocalDate favoriteDate) {
        this.person = person;
        this.event = event;
        this.favoriteDate = favoriteDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public LocalDate getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(LocalDate favoriteDate) {
        this.favoriteDate = favoriteDate;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Favourite))
            return false;
        return person==((Favourite) o).person&& event==((Favourite) o).event;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(person)*31+System.identityHashCode(event);
    }
}
