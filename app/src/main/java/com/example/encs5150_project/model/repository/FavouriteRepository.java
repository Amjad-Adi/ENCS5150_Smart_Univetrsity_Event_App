package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.Favourite;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.FavouriteContract;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FavouriteRepository {
    private final DataBaseHelper dataBaseHelper;
    private static final DateTimeFormatter SQLITE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);

    public FavouriteRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper = dataBaseHelper;
    }

    private OffsetDateTime parseDateSafely(String dateString) {
        if (dateString == null) return null;
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, SQLITE_FORMATTER);
            return localDateTime.atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            return OffsetDateTime.parse(dateString);
        }
    }

    public void insert(Favourite favourite) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteContract.COLUMN_USER_ID, favourite.getUserId());
        contentValues.put(FavouriteContract.COLUMN_EVENT_ID, favourite.getEventId());
        String dateStr = favourite.getFavoriteDate() != null ? favourite.getFavoriteDate().format(SQLITE_FORMATTER) : OffsetDateTime.now(ZoneOffset.UTC).format(SQLITE_FORMATTER);
        contentValues.put(FavouriteContract.COLUMN_DATE, dateStr);
        if (db.insert(FavouriteContract.TABLE_NAME, null, contentValues) == -1)
            throw new RuntimeException("Failed to insert favourite into SQLite.");
    }

    public Favourite findById(long eventId, long userId) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + FavouriteContract.TABLE_NAME +
                        " WHERE " + FavouriteContract.COLUMN_USER_ID + " = ? AND " + FavouriteContract.COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(eventId)});
        try {
            if (!cursor.moveToFirst())
                return null;
            return new Favourite(
                    cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_USER_ID)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_EVENT_ID)),
                    parseDateSafely(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_DATE)))
            );
        } finally {
            cursor.close();
        }
    }

    public List<Favourite> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FavouriteContract.TABLE_NAME, null);
        List<Favourite> favouriteList = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                favouriteList.add(new Favourite(
                        cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_USER_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_EVENT_ID)),
                        parseDateSafely(cursor.getString(cursor.getColumnIndexOrThrow(FavouriteContract.COLUMN_DATE)))
                ));
            }
            return favouriteList;
        } finally {
            cursor.close();
        }
    }

    public List<Long> getFavoriteEventIds(long userId) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + FavouriteContract.COLUMN_EVENT_ID +
                        " FROM " + FavouriteContract.TABLE_NAME +
                        " WHERE " + FavouriteContract.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        List<Long> favoriteIds = new ArrayList<>();
        try {
            while (cursor.moveToNext()) {
                favoriteIds.add(cursor.getLong(0));
            }
            return favoriteIds;
        } finally {
            cursor.close();
        }
    }

    public void delete(long userId, long eventId) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        if (db.delete(FavouriteContract.TABLE_NAME, FavouriteContract.COLUMN_USER_ID + " = ? and " + FavouriteContract.COLUMN_EVENT_ID + " = ?", new String[]{String.valueOf(userId), String.valueOf(eventId)}) == 0)
            throw new RuntimeException("No favourite found with user id " + userId + " and event id " + eventId);
    }
}