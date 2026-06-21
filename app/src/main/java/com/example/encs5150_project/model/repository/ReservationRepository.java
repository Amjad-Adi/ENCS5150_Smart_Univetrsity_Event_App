package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;

import java.time.OffsetDateTime;
import java.util.*;

public class ReservationRepository{
    private final DataBaseHelper dataBaseHelper;
    public ReservationRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }
    public void insert(Reservation reservation) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReservationContract.COLUMN_USER_ID, reservation.getUserId());
        contentValues.put(ReservationContract.COLUMN_EVENT_ID, reservation.getEventId());
        contentValues.put(ReservationContract.COLUMN_TYPE, reservation.getReservationType().name());
        contentValues.put(ReservationContract.COLUMN_ADDITIONAL_INFO, reservation.getReservationAdditionalInfo());
        contentValues.put(ReservationContract.COLUMN_PARTICIPATION_COUNT, reservation.getParticipationCount());
        long generatedId = db.insert(ReservationContract.TABLE_NAME, null, contentValues);
        if (generatedId == -1)
            throw new RuntimeException("Failed to insert reservation into SQLite.");
        reservation.setId(generatedId);
    }

    public void update(Reservation reservation) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ReservationContract.COLUMN_TYPE,reservation.getReservationType().name());
        contentValues.put(ReservationContract.COLUMN_ADDITIONAL_INFO,reservation.getReservationAdditionalInfo());
        contentValues.put(ReservationContract.COLUMN_PARTICIPATION_COUNT,reservation.getParticipationCount());
        contentValues.put(ReservationContract.COLUMN_STATUS,reservation.getReservationStatus().name());
        if (db.update(ReservationContract.TABLE_NAME,contentValues,ReservationContract.COLUMN_ID+" = ?",new String[]{String.valueOf(reservation.getId())})== 0)
            throw new RuntimeException("No reservation found with id " + reservation.getId());
    }

    public Reservation findById(long id) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT *" +
                    " FROM "+ ReservationContract.TABLE_NAME+
                    " WHERE " + ReservationContract.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        try {
            if (!cursor.moveToFirst())
                return null;
            return new Reservation(cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)), ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))), cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)), ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))),cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)), OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE))));
        } finally {
            cursor.close();
        }
    }

    public List<Reservation> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                    "SELECT *" +
                        " FROM "+ ReservationContract.TABLE_NAME, null);
        List<Reservation>reservationList=new ArrayList<>();
        try {
            while(cursor.moveToNext())
                reservationList.add(new Reservation(cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)), ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))), cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)),ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))), cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)) ,OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE)))));
            return reservationList;
        } finally {
            cursor.close();
        }
    }

    public void changeStatus(long id, ReservationStatus status) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ReservationContract.COLUMN_STATUS,status.name());
        if (db.update(ReservationContract.TABLE_NAME,contentValues,ReservationContract.COLUMN_ID+" = ?",new String[]{String.valueOf(id)})==0)
            throw new RuntimeException("No reservation found with id " + id);
    }
    public List<Reservation> search(String searchBy, boolean isAscending, String query) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? ReservationContract.COLUMN_ID : searchBy;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + ReservationContract.TABLE_NAME +
                        " WHERE " + orderByColumn + " LIKE ? " +
                        " ORDER BY " + orderByColumn + " COLLATE NOCASE " + (isAscending ? "ASC" : "DESC"),
                new String[]{"%" + query + "%"});
        List<Reservation> reservationList = new ArrayList<>();
        try {
            while(cursor.moveToNext()) {
                reservationList.add(new Reservation(cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)),cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)),  ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))), cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)) , ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))),cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)),OffsetDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE)))));
            }
            return reservationList;
        } finally {
            cursor.close();
        }
    }
}
