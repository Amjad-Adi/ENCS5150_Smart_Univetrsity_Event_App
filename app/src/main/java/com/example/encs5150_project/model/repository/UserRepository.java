package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class UserRepository implements CrudRepository<User> {
    private final DataBaseHelper dataBaseHelper;
    public UserRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(User entity) {
        return false;
    }

    @Override
    public void update(User entity) {

    }

    @Override
    public User findById(long id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
