package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.CrudRepository;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;

import java.util.Collections;
import java.util.List;

public class EventRepository implements CrudRepository<Event> {
    private final DataBaseHelper dataBaseHelper;
    public EventRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    @Override
    public boolean save(Event event) {
        SQLiteDatabase db=dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(EventContract.COLUMN_ID,event.getId());
        contentValues.put(EventContract.COLUMN_TITLE,event.getTitle());
        contentValues.put(EventContract.COLUMN_DESCRIPTION,event.getDescription());
        contentValues.put(EventContract.COLUMN_CATEGORY,event.getCategory());
        contentValues.put(EventContract.COLUMN_DATE,event.getDate().toString());
        contentValues.put(EventContract.COLUMN_TIME,event.getTime().toString());
        contentValues.put(EventContract.COLUMN_LOCATION,event.getLocation());
        contentValues.put(EventContract.COLUMN_TOTAL_SEATS,event.getTotalSeats());
        contentValues.put(EventContract.COLUMN_IMAGE_PATH,event.getImagePath());
        return db.insert(EventContract.TABLE_NAME,null,contentValues)==-1;
    }

    @Override
    public void update(Event event) {

    }

    @Override
    public Event findById(long id) {
        return null;
    }

    @Override
    public List<Event> findAll() {
        return Collections.emptyList();
    }

    @Override
    public void delete(long id) {

    }
}
