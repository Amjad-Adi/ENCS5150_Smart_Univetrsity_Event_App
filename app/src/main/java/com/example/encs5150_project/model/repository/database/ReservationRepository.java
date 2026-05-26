package com.example.encs5150_project.model.repository.database;

import com.example.encs5150_project.model.entity.Reservation;

import java.util.Collections;
import java.util.List;

public class ReservationRepository implements DataBaseRepository<Reservation> {
    @Override
    public void save(Reservation entity) {

    }

    @Override
    public void update(Reservation entity) {

    }

    @Override
    public Reservation findById(long id) {
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
