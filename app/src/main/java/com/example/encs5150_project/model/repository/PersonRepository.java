package com.example.encs5150_project.model.repository;

import com.example.encs5150_project.model.entity.Person;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;

import java.util.Collections;
import java.util.List;

public class PersonRepository implements CrudRepository<Person> {
    private final DataBaseHelper dataBaseHelper;
    public PersonRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Person entity) {
        return false;
    }

    @Override
    public void update(Person entity) {

    }

    @Override
    public Person findById(long id) {
        return null;
    }

    @Override
    public List<Person> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
