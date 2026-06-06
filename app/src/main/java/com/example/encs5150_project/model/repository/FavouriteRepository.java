package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.Favourite;
import com.example.encs5150_project.model.entity.Review;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.FavouriteContract;
import com.example.encs5150_project.model.repository.database.contracts.ReviewContract;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavouriteRepository {
    private final DataBaseHelper dataBaseHelper;
    public FavouriteRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }

    public void insert(Favourite favourite) {
            SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavouriteContract.COLUMN_USER_ID, favourite.getUserId());
            contentValues.put(FavouriteContract.COLUMN_EVENT_ID, favourite.getEventId());
            if(db.insert(FavouriteContract.TABLE_NAME, null, contentValues)==-1)
                throw new RuntimeException("Failed to insert favourite into SQLite.");
        }

    public Favourite findById(long eventId, long userId) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(
                    "SELECT *" +
                        " FROM "+FavouriteContract.TABLE_NAME+
                        " WHERE "+FavouriteContract.COLUMN_USER_ID+" = ? AND "+FavouriteContract.COLUMN_EVENT_ID+" = ?",new String[]{String.valueOf(userId),String.valueOf(eventId)});
        try{
            if(!cursor.moveToFirst())
                return null;
            return new Favourite(cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_USER_ID)),cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_EVENT_ID)),OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_DATE))));
        }finally {
            cursor.close();
        }
    }

    public List<Favourite> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(
                    "SELECT *" +
                        " FROM "+ FavouriteContract.TABLE_NAME,null);
        List<Favourite>favouriteList=new ArrayList<>();
        try {
            while(cursor.moveToNext())
                favouriteList.add(new Favourite(cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_USER_ID)),cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_EVENT_ID)), OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_DATE)))));
            return favouriteList;
        } finally {
            cursor.close();
        }
    }

    public void delete(long userId,long eventId) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        if (db.delete(FavouriteContract.TABLE_NAME,FavouriteContract.COLUMN_USER_ID+" = ? and "+FavouriteContract.COLUMN_EVENT_ID+" = ?",new String[]{String.valueOf(userId),String.valueOf(eventId)})==0)
            throw new RuntimeException("No favourite found with user id " + userId+" and event id "+eventId);
    }
}
