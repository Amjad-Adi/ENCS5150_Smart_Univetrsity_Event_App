package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.UserReservationSummary;
import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReservationRepository {
    private final DataBaseHelper dataBaseHelper;
    private static final DateTimeFormatter SQLITE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

    public ReservationRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper = dataBaseHelper;
    }

    private OffsetDateTime parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, SQLITE_FORMATTER);
            return localDateTime.atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            return OffsetDateTime.parse(dateString);
        }
    }

    public void insert(Reservation reservation) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReservationContract.COLUMN_USER_ID, reservation.getUserId());
        contentValues.put(ReservationContract.COLUMN_EVENT_ID, reservation.getEventId());
        contentValues.put(ReservationContract.COLUMN_TYPE, reservation.getReservationType().name());
        contentValues.put(ReservationContract.COLUMN_ADDITIONAL_INFO, reservation.getReservationAdditionalInfo());
        contentValues.put(ReservationContract.COLUMN_PARTICIPATION_COUNT, reservation.getParticipationCount());
        String dateStr = reservation.getReservationDate() != null ? reservation.getReservationDate().format(SQLITE_FORMATTER) : OffsetDateTime.now(ZoneOffset.UTC).format(SQLITE_FORMATTER);
        contentValues.put(ReservationContract.COLUMN_DATE, dateStr);
        long generatedId = db.insert(ReservationContract.TABLE_NAME, null, contentValues);
        if (generatedId == -1)
            throw new RuntimeException("Failed to insert reservation into SQLite.");
        reservation.setId(generatedId);
    }

    public void update(Reservation reservation) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReservationContract.COLUMN_TYPE, reservation.getReservationType().name());
        contentValues.put(ReservationContract.COLUMN_ADDITIONAL_INFO, reservation.getReservationAdditionalInfo());
        contentValues.put(ReservationContract.COLUMN_PARTICIPATION_COUNT, reservation.getParticipationCount());
        contentValues.put(ReservationContract.COLUMN_STATUS, reservation.getReservationStatus().name());
        if (reservation.getReservationDate() != null) {
            contentValues.put(ReservationContract.COLUMN_DATE, reservation.getReservationDate().format(SQLITE_FORMATTER));
        }
        if (db.update(ReservationContract.TABLE_NAME, contentValues, ReservationContract.COLUMN_ID + " = ?", new String[]{String.valueOf(reservation.getId())}) == 0)
            throw new RuntimeException("No reservation found with id " + reservation.getId());
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
                reservationList.add(new Reservation(
                        cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)),
                        ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))),
                        cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)),
                        ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))),
                        cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)),
                        parseDate(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE)))
                ));
            }
            return reservationList;
        } finally {
            cursor.close();
        }
    }
    public void autoUpdateCompletedReservations() {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReservationContract.COLUMN_STATUS, ReservationStatus.COMPLETED.name());
        OffsetDateTime thresholdTime = OffsetDateTime.now(ZoneOffset.UTC).minusHours(24);
        String thresholdString = thresholdTime.format(SQLITE_FORMATTER);
        db.update(ReservationContract.TABLE_NAME, contentValues, ReservationContract.COLUMN_STATUS + " = ? AND " + ReservationContract.COLUMN_DATE + " <= ?", new String[]{ReservationStatus.CONFIRMED.name(), thresholdString});
    }
    public List<UserReservationSummary> searchUserReservations(long userId, String searchBy, boolean isAscending, String query) {
        autoUpdateCompletedReservations();
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String orderByColumn = (searchBy == null || searchBy.trim().isEmpty()) ? "r." + ReservationContract.COLUMN_ID : searchBy;
        if (orderByColumn.equalsIgnoreCase("event_title")) {
            orderByColumn = "e.title";
        } else if (!orderByColumn.contains(".")) {
            orderByColumn = "r." + orderByColumn;
        }
        String sql = "SELECT r.*, e."+ EventContract.COLUMN_TITLE +" AS event_title" +
                " FROM " + ReservationContract.TABLE_NAME + " r" +
                " JOIN Event e ON r." + ReservationContract.COLUMN_EVENT_ID + " = e."+EventContract.COLUMN_ID +
                " WHERE r." + ReservationContract.COLUMN_USER_ID + " = ? AND " + orderByColumn + " LIKE ?" +
                " ORDER BY " + orderByColumn + " COLLATE NOCASE " + (isAscending ? "ASC" : "DESC");

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId), "%" + query + "%"});
        List<UserReservationSummary> summaryList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                Reservation reservation = new Reservation(cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)), ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))), cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)), ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))), cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)), parseDate(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE))));
                String eventTitle = cursor.getString(cursor.getColumnIndexOrThrow("event_title"));
                summaryList.add(new UserReservationSummary(reservation, eventTitle));
            }
            return summaryList;
        } finally {
            cursor.close();
        }
    }
    public int getTotalAttendeesCount() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + ReservationContract.COLUMN_PARTICIPATION_COUNT + ") " +
                        "FROM " + ReservationContract.TABLE_NAME,
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

    public Map<Integer, Integer> getDailyReservationsForMonth(String yearMonth) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Map<Integer, Integer> dailyStats = new HashMap<>();
        String sql = "SELECT strftime('%d', " + ReservationContract.COLUMN_DATE + ") AS reservation_day, " +
                        "COUNT(" + ReservationContract.COLUMN_ID + ") AS reservation_count " +
                        "FROM " + ReservationContract.TABLE_NAME + " " +
                        "WHERE strftime('%Y-%m', " + ReservationContract.COLUMN_DATE + ") = ? " +
                        "GROUP BY strftime('%d', " + ReservationContract.COLUMN_DATE + ")";
        Cursor cursor = db.rawQuery(sql, new String[]{yearMonth});
        try {
            while (cursor.moveToNext()) {
                int day = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("reservation_day")));
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("reservation_count"));
                dailyStats.put(day, count);
            }
        } finally {
            cursor.close();
        }

        return dailyStats;
    }

    public Map<Integer, Integer> getMonthlyParticipationStats(String year) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Map<Integer, Integer> monthlyStats = new HashMap<>();
        String sql =
                "SELECT strftime('%m', " + ReservationContract.COLUMN_DATE + ") AS reservation_month, " +
                        "SUM(" + ReservationContract.COLUMN_PARTICIPATION_COUNT + ") AS total_participation " +
                        "FROM " + ReservationContract.TABLE_NAME + " " +
                        "WHERE strftime('%Y', " + ReservationContract.COLUMN_DATE + ") = ? " +
                        "GROUP BY strftime('%m', " + ReservationContract.COLUMN_DATE + ")";
        Cursor cursor = db.rawQuery(sql, new String[]{year});
        try {
            while (cursor.moveToNext()) {
                int month = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("reservation_month")));
                int total = cursor.getInt(cursor.getColumnIndexOrThrow("total_participation"));
                monthlyStats.put(month, total);
            }
        } finally {
            cursor.close();
        }

        return monthlyStats;
    }

    public Map<String, Integer> getUserCategoryStats(long userId) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT e." + EventContract.COLUMN_CATEGORY + ", COUNT(r." + ReservationContract.COLUMN_ID + ") " +
                "FROM " + ReservationContract.TABLE_NAME + " r " +
                "JOIN Event e ON r." + ReservationContract.COLUMN_EVENT_ID + " = e." + EventContract.COLUMN_ID +
                " WHERE r." + ReservationContract.COLUMN_USER_ID + " = ? " +
                "GROUP BY e." + EventContract.COLUMN_CATEGORY;
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId)});

        try {
            int categoryIndex = cursor.getColumnIndexOrThrow(EventContract.COLUMN_CATEGORY);
            int countIndex = 1;

            while (cursor.moveToNext()) {
                String category = cursor.getString(categoryIndex);
                int count = cursor.getInt(countIndex);
                if (category != null) {
                    stats.put(category, count);
                }
            }
        } finally {
            cursor.close();
        }

        return stats;
    }
    public List<UserReservationSummary> getUserRecentReservations(long userId, int limit) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        List<UserReservationSummary> summaryList = new ArrayList<>();
        String sql = "SELECT r.*, e." + EventContract.COLUMN_TITLE + " AS event_title " +
                "FROM " + ReservationContract.TABLE_NAME + " r " +
                "JOIN Event e ON r." + ReservationContract.COLUMN_EVENT_ID + " = e." + EventContract.COLUMN_ID +
                " WHERE r." + ReservationContract.COLUMN_USER_ID + " = ? " +
                " ORDER BY r." + ReservationContract.COLUMN_DATE + " DESC " +
                " LIMIT ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(limit)});
        try {
            while (cursor.moveToNext()) {
                Reservation reservation = new Reservation(cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_USER_ID)), cursor.getLong(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_EVENT_ID)), ReservationType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_TYPE))), cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_PARTICIPATION_COUNT)), ReservationStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_STATUS))), cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_ADDITIONAL_INFO)), parseDate(cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.COLUMN_DATE))));
                String eventTitle = cursor.getString(cursor.getColumnIndexOrThrow("event_title"));
                summaryList.add(new UserReservationSummary(reservation, eventTitle));
            }
        } finally {
            cursor.close();
        }
        return summaryList;
    }
}