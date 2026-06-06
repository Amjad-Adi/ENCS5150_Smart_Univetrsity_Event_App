package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.Review;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.ReviewContract;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewRepository {
    private final DataBaseHelper dataBaseHelper;
    public ReviewRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    public void insert(Review review) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ReviewContract.COLUMN_RESERVATION_ID,review.getReservationId());
        contentValues.put(ReviewContract.COLUMN_RATING,review.getRating());
        contentValues.put(ReviewContract.COLUMN_TEXT,review.getText());
        if(db.insert(ReviewContract.TABLE_NAME,null,contentValues)==-1)
            throw new RuntimeException("Failed to insert review into SQLite.");
    }

    public void update(Review review) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ReviewContract.COLUMN_RATING,review.getRating());
        contentValues.put(ReviewContract.COLUMN_TEXT,review.getText());
        if (db.update(ReviewContract.TABLE_NAME,contentValues,ReviewContract.COLUMN_RESERVATION_ID+" = ?",new String[]{String.valueOf(review.getReservationId())})==0)
            throw new RuntimeException("No review found with reservation id " + review.getReservationId());
    }

    public Review findById(long reservationId) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(
                "SELECT *" +
                    " FROM "+ReviewContract.TABLE_NAME+
                    " WHERE "+ReviewContract.COLUMN_RESERVATION_ID+" = ?",new String[]{String.valueOf(reservationId)});
        try{
            if(!cursor.moveToFirst())
                return null;
            return new Review(cursor.getLong(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_RESERVATION_ID)), cursor.getDouble(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_RATING)), cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_TEXT)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_STATUS))), OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_DATE))));
        } finally {
            cursor.close();
        }
    }
    public List<Review> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(
                "SELECT *" +
                    " FROM "+ReviewContract.TABLE_NAME,null);
        List<Review>reviewList=new ArrayList<>();
        try {
            while(cursor.moveToNext())
                reviewList.add(new Review(cursor.getLong(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_RESERVATION_ID)), cursor.getDouble(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_RATING)), cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_TEXT)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_STATUS))), OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(ReviewContract.COLUMN_DATE)))));
            return reviewList;
        } finally {
            cursor.close();
        }
    }
    public void changeStatus(long reservationId, EntityStatus status){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ReviewContract.COLUMN_STATUS,status.name());
        if (db.update(ReviewContract.TABLE_NAME,contentValues,ReviewContract.COLUMN_RESERVATION_ID+" = ?",new String[]{String.valueOf(reservationId)})==0)
            throw new RuntimeException("No review found with reservation id " + reservationId);
    }
}
