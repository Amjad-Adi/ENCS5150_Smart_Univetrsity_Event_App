package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.Favourite;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class FavouriteRepository implements CrudRepository<Favourite> {
    private final DataBaseHelper dataBaseHelper;
    public FavouriteRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Favourite entity) {
        return false;
    }

    @Override
    public void update(Favourite entity) {

    }

    @Override
    public Favourite findById(long id) {
        return null;
    }

    @Override
    public List<Favourite> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
