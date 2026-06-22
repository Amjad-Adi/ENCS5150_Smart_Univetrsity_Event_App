package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.*;

import java.time.*;
import java.util.*;

public class EventRepository {
    private final DataBaseHelper dataBaseHelper;

    public EventRepository(DataBaseHelper dataBaseHelper) {
        this.dataBaseHelper = dataBaseHelper;
    }

    public void insert(Event event) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.COLUMN_ID, event.getId());
        contentValues.put(EventContract.COLUMN_TITLE, event.getTitle());
        contentValues.put(EventContract.COLUMN_DESCRIPTION, event.getDescription());
        contentValues.put(EventContract.COLUMN_CATEGORY, event.getCategory());
        contentValues.put(EventContract.COLUMN_DATE, event.getDate().toString());
        contentValues.put(EventContract.COLUMN_TIME, event.getTime().toString());
        contentValues.put(EventContract.COLUMN_LOCATION, event.getLocation());
        contentValues.put(EventContract.COLUMN_TOTAL_SEATS, event.getTotalSeats());
        contentValues.put(EventContract.COLUMN_IMAGE_PATH, event.getImagePath());
        if (db.insertWithOnConflict(EventContract.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE) == -1)
            throw new RuntimeException("Failed to insert event into SQLite.");
    }
    public void upsertFromApi(Event event) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.COLUMN_TITLE, event.getTitle());
        contentValues.put(EventContract.COLUMN_DESCRIPTION, event.getDescription());
        contentValues.put(EventContract.COLUMN_CATEGORY, event.getCategory());
        contentValues.put(EventContract.COLUMN_DATE, event.getDate().toString());
        contentValues.put(EventContract.COLUMN_TIME, event.getTime().toString());
        contentValues.put(EventContract.COLUMN_LOCATION, event.getLocation());
        contentValues.put(EventContract.COLUMN_TOTAL_SEATS, event.getTotalSeats());
        contentValues.put(EventContract.COLUMN_IMAGE_PATH, event.getImagePath());
        db.beginTransaction();
        try {
            int rowsAffected = db.update(EventContract.TABLE_NAME, contentValues, EventContract.COLUMN_ID + " = ?", new String[]{String.valueOf(event.getId())});
            if (rowsAffected == 0) {
                contentValues.put(EventContract.COLUMN_ID, event.getId());
                db.insert(EventContract.TABLE_NAME, null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
    public void update(Event event) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.COLUMN_TITLE, event.getTitle());
        contentValues.put(EventContract.COLUMN_DESCRIPTION, event.getDescription());
        contentValues.put(EventContract.COLUMN_CATEGORY, event.getCategory());
        contentValues.put(EventContract.COLUMN_DATE, event.getDate().toString());
        contentValues.put(EventContract.COLUMN_TIME, event.getTime().toString());
        contentValues.put(EventContract.COLUMN_LOCATION, event.getLocation());
        contentValues.put(EventContract.COLUMN_TOTAL_SEATS, event.getTotalSeats());
        contentValues.put(EventContract.COLUMN_IMAGE_PATH, event.getImagePath());
        contentValues.put(EventContract.COLUMN_STATUS, event.getStatus().name());
        if (db.update(EventContract.TABLE_NAME, contentValues, EventContract.COLUMN_ID + " = ?", new String[]{String.valueOf(event.getId())}) == 0)
            throw new RuntimeException("No event found with id " + event.getId());
    }

    public Event findById(long id) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * " +
                        " FROM " + EventContract.TABLE_NAME +
                        " WHERE " + EventContract.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        try {
            if (!cursor.moveToFirst())
                return null;
            return new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
        } finally {
            cursor.close();
        }
    }

    public List<Event> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT *" +
                        " FROM " + EventContract.TABLE_NAME, null);
        List<Event> eventList = new ArrayList<>();
        try {
            while (cursor.moveToNext())
                eventList.add(new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS)))));
            return eventList;
        } finally {
            cursor.close();
        }
    }

    public void changeStatus(long id, EntityStatus status) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventContract.COLUMN_STATUS, status.name());
        if (db.update(EventContract.TABLE_NAME, contentValues, EventContract.COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) == 0)
            throw new RuntimeException("No event found with id " + id);
    }

    public List<EventSummary> searchEventSummariesForAdmin(String searchBy, boolean isAscending, String queryStr) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? EventContract.COLUMN_TITLE : searchBy;

        String sql =
                "SELECT e.*, " +
                        "COALESCE(r.booked_seats, 0) AS booked_seats, " +
                        "COALESCE(f.favorite_count, 0) AS favorite_count " +
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
                        "       " + FavouriteContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       COUNT(*) AS favorite_count " +
                        "   FROM " + FavouriteContract.TABLE_NAME + " " +
                        "   GROUP BY " + FavouriteContract.COLUMN_EVENT_ID +
                        ") f ON f.event_id = e." + EventContract.COLUMN_ID + " " +
                        "WHERE e." + orderByColumn + " LIKE ? " +
                        "ORDER BY e." + orderByColumn + " COLLATE NOCASE " +
                        (isAscending ? "ASC" : "DESC");

        Cursor cursor = db.rawQuery(sql, new String[]{"%" + queryStr + "%"});
        List<EventSummary> summaryList = new ArrayList<>();

        try {
            int imgIndex = cursor.getColumnIndex(EventContract.COLUMN_IMAGE_PATH);
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
                if (imgIndex != -1 && !cursor.isNull(imgIndex)) {
                    event.setImagePath(cursor.getString(imgIndex));
                }
                int bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                int favoriteCount = cursor.getInt(cursor.getColumnIndexOrThrow("favorite_count"));
                boolean isEnabled = event.getStatus() == EntityStatus.ENABLED;
                summaryList.add(new EventSummary(event, bookedSeats, favoriteCount, false, isEnabled));
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
                        "COALESCE(f.favorite_count, 0) AS favorite_count, " +
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
                        "       " + FavouriteContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       COUNT(*) AS favorite_count " +
                        "   FROM " + FavouriteContract.TABLE_NAME + " " +
                        "   GROUP BY " + FavouriteContract.COLUMN_EVENT_ID +
                        ") f ON f.event_id = e." + EventContract.COLUMN_ID + " " +
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
                if (imgIndex != -1 && !cursor.isNull(imgIndex)) {
                    event.setImagePath(cursor.getString(imgIndex));
                }
                int bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                int favoriteCount = cursor.getInt(cursor.getColumnIndexOrThrow("favorite_count"));
                boolean isReserved = cursor.getInt(cursor.getColumnIndexOrThrow("is_reserved")) == 1;
                boolean isEnabled = event.getStatus() == EntityStatus.ENABLED;
                summaryList.add(new EventSummary(event, bookedSeats, favoriteCount, isReserved, isEnabled));
            }
            return summaryList;
        } finally {
            cursor.close();
        }
    }
    public List<EventSummary> searchRecommendedEventSummariesForUser(String searchBy, boolean isAscending, String queryStr, User user) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? EventContract.COLUMN_DATE : searchBy;
        String currentDateStr = LocalDate.now().toString();
        String sql =
                "SELECT e.*, " +
                        "COALESCE(r.booked_seats, 0) AS booked_seats, " +
                        "COALESCE(f.favorite_count, 0) AS favorite_count, " +
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
                        "       " + FavouriteContract.COLUMN_EVENT_ID + " AS event_id, " +
                        "       COUNT(*) AS favorite_count " +
                        "   FROM " + FavouriteContract.TABLE_NAME + " " +
                        "   GROUP BY " + FavouriteContract.COLUMN_EVENT_ID +
                        ") f ON f.event_id = e." + EventContract.COLUMN_ID + " " +
                        "LEFT JOIN ( " +
                        "   SELECT DISTINCT " + ReservationContract.COLUMN_EVENT_ID + " AS event_id " +
                        "   FROM " + ReservationContract.TABLE_NAME + " " +
                        "   WHERE " + ReservationContract.COLUMN_USER_ID + " = ? " +
                        ") ur ON ur.event_id = e." + EventContract.COLUMN_ID + " " +
                        "WHERE e." + EventContract.COLUMN_CATEGORY + " IN ( " +
                        "   SELECT DISTINCT cat_e." + EventContract.COLUMN_CATEGORY +
                        "   FROM " + ReservationContract.TABLE_NAME + " cat_r " +
                        "   JOIN " + EventContract.TABLE_NAME + " cat_e ON cat_r." + ReservationContract.COLUMN_EVENT_ID + " = cat_e." + EventContract.COLUMN_ID + " " +
                        "   WHERE cat_r." + ReservationContract.COLUMN_USER_ID + " = ? " +
                        ") " +
                        "AND e." + EventContract.COLUMN_DATE + " >= ? " +
                        "AND e." + orderByColumn + " LIKE ? " +
                        "ORDER BY e." + orderByColumn + " COLLATE NOCASE " +
                        (isAscending ? "ASC" : "DESC");
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(user.getId()), String.valueOf(user.getId()), currentDateStr, "%" + queryStr + "%"});
        List<EventSummary> summaryList = new ArrayList<>();
        try {
            int imgIndex = cursor.getColumnIndex(EventContract.COLUMN_IMAGE_PATH);
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
                if (imgIndex != -1 && !cursor.isNull(imgIndex)) {
                    event.setImagePath(cursor.getString(imgIndex));
                }
                int bookedSeats = cursor.getInt(cursor.getColumnIndexOrThrow("booked_seats"));
                int favoriteCount = cursor.getInt(cursor.getColumnIndexOrThrow("favorite_count"));
                boolean isReserved = cursor.getInt(cursor.getColumnIndexOrThrow("is_reserved")) == 1;
                boolean isEnabled = event.getStatus() == EntityStatus.ENABLED;
                summaryList.add(new EventSummary(event, bookedSeats, favoriteCount, isReserved, isEnabled));
            }
            return summaryList;
        } finally {
            cursor.close();
        }
    }
    public int getTotalEventsCount() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + EventContract.TABLE_NAME,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }

    public Map<String, Integer> getEventCategoryCounts() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Map<String, Integer> categoryCount = new HashMap<>();
        String sql = "SELECT " + EventContract.COLUMN_CATEGORY +
                        ", COUNT(" + EventContract.COLUMN_ID + ") AS event_count " +
                        "FROM " + EventContract.TABLE_NAME +
                        " GROUP BY " + EventContract.COLUMN_CATEGORY;
        Cursor cursor = db.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                String category = cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("event_count"));
                if (category != null) {
                    categoryCount.put(category, count);
                }
            }
        } finally {
            cursor.close();
        }

        return categoryCount;
    }
    public List<Event> getHighDemandEvents(int limit) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        List<Event> highDemandEvents = new ArrayList<>();
        String sql = "SELECT e.*, SUM(r." + ReservationContract.COLUMN_PARTICIPATION_COUNT + ") as total_demand " +
                "FROM Event e " +
                "JOIN " + ReservationContract.TABLE_NAME + " r ON e." + EventContract.COLUMN_ID + " = r." + ReservationContract.COLUMN_EVENT_ID + " " +
                "GROUP BY e." + EventContract.COLUMN_ID + " " +
                "ORDER BY total_demand DESC " +
                "LIMIT ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(limit)});
        try {
            while (cursor.moveToNext()) {
                Event event = new Event(cursor.getLong(cursor.getColumnIndexOrThrow(EventContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TITLE)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DESCRIPTION)), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY)), LocalDate.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_DATE))), LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TIME))), cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_LOCATION)), cursor.getInt(cursor.getColumnIndexOrThrow(EventContract.COLUMN_TOTAL_SEATS)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(EventContract.COLUMN_STATUS))));
                highDemandEvents.add(event);
            }
        } finally {
            cursor.close();
        }
        return highDemandEvents;
    }
}