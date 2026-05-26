package com.example.encs5150_project.model.repository.database;

import java.util.List;

public interface DataBaseRepository<T> {
    void save(T entity);
    void update(T entity);
    T findById(long id);
    List<T> findAll();
    void delete(long id);
}
