package com.example.encs5150_project.model.repository.database;

import com.example.encs5150_project.model.entity.Person;

import java.util.Collections;
import java.util.List;

public class PersonRepository implements DataBaseRepository<Person>{

    @Override
    public void save(Person entity) {

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
