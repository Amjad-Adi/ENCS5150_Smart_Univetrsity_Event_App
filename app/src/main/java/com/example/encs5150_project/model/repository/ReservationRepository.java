package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.Reservation;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class ReservationRepository implements CrudRepository<Reservation> {
    private final DataBaseHelper dataBaseHelper;
    public ReservationRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Reservation entity) {
        return false;
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
