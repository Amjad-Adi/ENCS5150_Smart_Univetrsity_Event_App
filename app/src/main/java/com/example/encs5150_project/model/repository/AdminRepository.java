package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class AdminRepository implements CrudRepository<Admin> {
    private final DataBaseHelper dataBaseHelper;
    public AdminRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Admin entity) {
        return false;
    }

    @Override
    public void update(Admin entity) {

    }

    @Override
    public Admin findById(long id) {
        return null;
    }

    @Override
    public List<Admin> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
