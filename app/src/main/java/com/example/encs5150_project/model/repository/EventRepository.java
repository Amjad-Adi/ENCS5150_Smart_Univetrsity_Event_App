package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import java.time.*;
import java.util.*;

public class EventRepository{
    private final DataBaseHelper dataBaseHelper;
    public EventRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }

    public void insert(Event event) {
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
        if(db.insert(EventContract.TABLE_NAME,null,contentValues)==-1)
            throw new RuntimeException("Failed to insert event into SQLite.");
    }

    public void update(Event event) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(EventContract.COLUMN_TITLE,event.getTitle());
        contentValues.put(EventContract.COLUMN_DESCRIPTION,event.getDescription());
        contentValues.put(EventContract.COLUMN_CATEGORY,event.getCategory());
        contentValues.put(EventContract.COLUMN_DATE,event.getDate().toString());
        contentValues.put(EventContract.COLUMN_TIME,event.getTime().toString());
        contentValues.put(EventContract.COLUMN_LOCATION,event.getLocation());
        contentValues.put(EventContract.COLUMN_TOTAL_SEATS,event.getTotalSeats());
        contentValues.put(EventContract.COLUMN_IMAGE_PATH,event.getImagePath());
        contentValues.put(EventContract.COLUMN_STATUS,event.getStatus().name());
        if (db.update(EventContract.TABLE_NAME,contentValues,EventContract.COLUMN_ID+" = ?",new String[]{String.valueOf(event.getId())})== 0)
            throw new RuntimeException("No event found with id " + event.getId());
    }

    public Event findById(long id) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(
                "SELECT * " +
                    " FROM "+ EventContract.TABLE_NAME+
                    " WHERE "+EventContract.COLUMN_ID+" = ?",new String[]{String.valueOf(id)});
        try{
            if(!cursor.moveToFirst())
                return null;
            return new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_IMAGE_PATH)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
        }finally {
            cursor.close();
        }
    }

    public List<Event> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT *" +
                    " FROM "+ EventContract.TABLE_NAME, null);
        List<Event>eventList=new ArrayList<>();
        try {
            while(cursor.moveToNext())
                eventList.add(new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_IMAGE_PATH)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS)))));
            return eventList;
        } finally {
            cursor.close();
        }
    }

    public void changeStatus(long id, EntityStatus status) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(EventContract.COLUMN_STATUS,status.name());
        if (db.update(EventContract.TABLE_NAME,contentValues,EventContract.COLUMN_ID+" = ?",new String[]{String.valueOf(id)})==0)
            throw new RuntimeException("No event found with id " + id);
    }
}
