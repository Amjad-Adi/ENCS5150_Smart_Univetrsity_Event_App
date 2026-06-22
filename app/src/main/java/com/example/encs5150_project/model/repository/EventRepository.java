package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;
import com.example.encs5150_project.model.repository.database.contracts.ReviewContract;

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
        if(db.insertWithOnConflict(EventContract.TABLE_NAME, null,contentValues, SQLiteDatabase.CONFLICT_REPLACE)==-1)
            throw new RuntimeException("Failed to insert person into SQLite.");
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
            return new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
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
                eventList.add(new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS)))));
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

    public List<EventSummary> searchEventSummariesForAdmin(String searchBy, boolean isAscending, String queryStr) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? EventContract.COLUMN_TITLE : searchBy;

        String sql =
                "SELECT e.*, " +
                        "COALESCE(r.booked_seats, 0) AS booked_seats, " +
                        "COALESCE(rv.avg_rating, 0) AS avg_rating, " +
                        "COALESCE(rv.review_count, 0) AS review_count " +
                        "FROM " + EventContract.TABLE_NAME + " e " +
                        "LEFT JOIN ( " +
                        "   SELECT " +
                        "       " + ReservationContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       SUM(" + ReservationContract.COLUMN_PARTICIPATION_COUNT + ") AS booked_seats " +
                        "   FROM " + ReservationContract.TABLE_NAME + " " +
                        "   GROUP BY " + ReservationContract.COLUMN_EVENT_ID +
                        ") r ON r.event_id = e." + EventContract.COLUMN_ID + " " +
                        "LEFT JOIN ( " +
                        "   SELECT " +
                        "       res." + ReservationContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       AVG(rev." + ReviewContract.COLUMN_RATING + ") AS avg_rating, " +
                        "       COUNT(rev." + ReviewContract.COLUMN_RESERVATION_ID + ") AS review_count " +
                        "   FROM " + ReviewContract.TABLE_NAME + " rev " +
                        "   INNER JOIN " + ReservationContract.TABLE_NAME + " res " +
                        "       ON rev." + ReviewContract.COLUMN_RESERVATION_ID + " = res." + ReservationContract.COLUMN_ID + " " +
                        "   GROUP BY res." + ReservationContract.COLUMN_EVENT_ID +
                        ") rv ON rv.event_id = e." + EventContract.COLUMN_ID + " " +

                        "WHERE e." + orderByColumn + " LIKE ? " +
                        "ORDER BY e." + orderByColumn + " COLLATE NOCASE " +
                        (isAscending ? "ASC" : "DESC");

        Cursor cursor = db.rawQuery(sql, new String[]{"%" + queryStr + "%"});
        List<EventSummary> summaryList = new ArrayList<>();

        try {
            int imgIndex = cursor.getColumnIndex(EventContract.COLUMN_IMAGE_PATH);
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
                if (imgIndex != -1 && !cursor.isNull(imgIndex))
                    event.setImagePath(cursor.getString(imgIndex));
                int bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                int reviewCount = cursor.getInt(cursor.getColumnIndexOrThrow("review_count"));
                int avgRatingIndex = cursor.getColumnIndexOrThrow("avg_rating");
                double avgRating = cursor.isNull(avgRatingIndex) ? 0.0 : cursor.getDouble(avgRatingIndex);
                summaryList.add(new EventSummary(event, bookedSeats, avgRating, reviewCount, false));
            }
            return summaryList;
        } finally {
            cursor.close();
        }
    }
    public List<EventSummary> searchEventSummariesForUser(String searchBy, boolean isAscending, String queryStr, User user) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? EventContract.COLUMN_TITLE : searchBy;

        String sql =
                "SELECT e.*, " +
                        "COALESCE(r.booked_seats, 0) AS booked_seats, " +
                        "COALESCE(rv.avg_rating, 0) AS avg_rating, " +
                        "COALESCE(rv.review_count, 0) AS review_count, " +
                        "CASE WHEN ur.event_id IS NULL THEN 0 ELSE 1 END AS is_reserved " +
                        "FROM " + EventContract.TABLE_NAME + " e " +
                        "LEFT JOIN ( " +
                        "   SELECT " +
                        "       " + ReservationContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       SUM(" + ReservationContract.COLUMN_PARTICIPATION_COUNT + ") AS booked_seats " +
                        "   FROM " + ReservationContract.TABLE_NAME + " " +
                        "   GROUP BY " + ReservationContract.COLUMN_EVENT_ID +
                        ") r ON r.event_id = e." + EventContract.COLUMN_ID + " " +
                        "LEFT JOIN ( " +
                        "   SELECT " +
                        "       res." + ReservationContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       AVG(rev." + ReviewContract.COLUMN_RATING + ") AS avg_rating, " +
                        "       COUNT(rev." + ReviewContract.COLUMN_RESERVATION_ID + ") AS review_count " +
                        "   FROM " + ReviewContract.TABLE_NAME + " rev " +
                        "   INNER JOIN " + ReservationContract.TABLE_NAME + " res " +
                        "       ON rev." + ReviewContract.COLUMN_RESERVATION_ID + " = res." + ReservationContract.COLUMN_ID + " " +
                        "   GROUP BY res." + ReservationContract.COLUMN_EVENT_ID +
                        ") rv ON rv.event_id = e." + EventContract.COLUMN_ID + " " +
                        "LEFT JOIN ( " +
                        "   SELECT DISTINCT " + ReservationContract.COLUMN_EVENT_ID + " AS event_id " +
                        "   FROM " + ReservationContract.TABLE_NAME + " " +
                        "   WHERE " + ReservationContract.COLUMN_USER_ID + " = ? " +
                        ") ur ON ur.event_id = e." + EventContract.COLUMN_ID + " " +
                        "WHERE e." + orderByColumn + " LIKE ? " +
                        "ORDER BY e." + orderByColumn + " COLLATE NOCASE " +
                        (isAscending ? "ASC" : "DESC");
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(user.getId()), "%" + queryStr + "%"});
        List<EventSummary> summaryList = new ArrayList<>();
        try {
            int imgIndex = cursor.getColumnIndex(EventContract.COLUMN_IMAGE_PATH);
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
                if (imgIndex != -1 && !cursor.isNull(imgIndex))
                    event.setImagePath(cursor.getString(imgIndex));
                int bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                int reviewCount = cursor.getInt(cursor.getColumnIndexOrThrow("review_count"));
                int avgRatingIndex = cursor.getColumnIndexOrThrow("avg_rating");
                double avgRating = cursor.isNull(avgRatingIndex) ? 0.0 : cursor.getDouble(avgRatingIndex);
                boolean isReserved = cursor.getInt(cursor.getColumnIndexOrThrow("is_reserved")) == 1;
                summaryList.add(new EventSummary(event, bookedSeats, avgRating, reviewCount, isReserved));
            }
            return summaryList;
        } finally {
            cursor.close();
        }
    }
}
