package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.Review;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class ReviewRepository implements CrudRepository<Review> {
    private final DataBaseHelper dataBaseHelper;
    public ReviewRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Review entity) {
        return false;
    }

    @Override
    public void update(Review entity) {

    }

    @Override
    public Review findById(long id) {
        return null;
    }

    @Override
    public List<Review> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
