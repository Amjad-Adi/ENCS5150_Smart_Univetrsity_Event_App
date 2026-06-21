package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.AdminContract;
import com.example.encs5150_project.model.repository.database.contracts.PersonContract;
import com.example.encs5150_project.model.repository.database.contracts.UserContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository {
    private final DataBaseHelper dataBaseHelper;

    public UserRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
    }

    public void insert(User user) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            PersonRepository personRepository = new PersonRepository(dataBaseHelper);
            personRepository.insert(user);
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserContract.COLUMN_ID, user.getId());
            contentValues.put(UserContract.COLUMN_MAJOR, user.getMajor().toString());
            contentValues.put(UserContract.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
            if(db.insert(UserContract.TABLE_NAME, null, contentValues) == -1)
                throw new RuntimeException("Failed to insert person into SQLite.");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void update(User user) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues personValues = new ContentValues();
        ContentValues userValues = new ContentValues();
        personValues.put(PersonContract.COLUMN_FIRST_NAME, user.getFirstName());
        personValues.put(PersonContract.COLUMN_LAST_NAME, user.getLastName());
        personValues.put(PersonContract.COLUMN_EMAIL, user.getEmail());
        personValues.put(PersonContract.COLUMN_PASSWORD, user.getPassword());
        personValues.put(PersonContract.COLUMN_GENDER, user.getGender().name());
        personValues.put(PersonContract.COLUMN_PROFILE_PICTURE_PATH, user.getProfilePicturePath());
        userValues.put(UserContract.COLUMN_MAJOR, user.getMajor().name());
        userValues.put(UserContract.COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        db.beginTransaction();
        try{
            if(db.update(PersonContract.TABLE_NAME, personValues, PersonContract.COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())}) == 0)
                throw new RuntimeException("No user found with id " + user.getId());
            if(db.update(UserContract.TABLE_NAME, userValues, UserContract.COLUMN_ID + " = ?", new String[]{String.valueOf(user.getId())}) == 0)
                throw new RuntimeException("No user found with id " + user.getId());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public User findById(long id) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT P.*, U."+UserContract.COLUMN_MAJOR+", U."+UserContract.COLUMN_PHONE_NUMBER+", U."+UserContract.COLUMN_ACCOUNT_STATUS +
                " FROM " + UserContract.TABLE_NAME + " U" +
                " JOIN " + PersonContract.TABLE_NAME + " P" +
                " ON U." + UserContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID +
                " WHERE U." + UserContract.COLUMN_ID + " =?", new String[]{String.valueOf(id)});
        try {
            if(!cursor.moveToFirst()) return null;
            User user = new User(cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)), PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))), UserMajor.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_MAJOR))), cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_PHONE_NUMBER)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ACCOUNT_STATUS))));
            int picIndex = cursor.getColumnIndex(PersonContract.COLUMN_PROFILE_PICTURE_PATH);
            if (picIndex != -1 && !cursor.isNull(picIndex)) {
                user.setProfilePicturePath(cursor.getString(picIndex));
            }
            return user;
        } finally {
            cursor.close();
        }
    }

    public List<User> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT P.*, U."+UserContract.COLUMN_MAJOR+", U."+UserContract.COLUMN_PHONE_NUMBER+", U."+UserContract.COLUMN_ACCOUNT_STATUS +
                        " FROM " + UserContract.TABLE_NAME + " U " +
                        "JOIN " + PersonContract.TABLE_NAME + " P " +
                        "ON U." + UserContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID,null);
        List<User> userList = new ArrayList<>();
        try {
            int picIndex = cursor.getColumnIndex(PersonContract.COLUMN_PROFILE_PICTURE_PATH);
            while(cursor.moveToNext()) {
                User user = new User(cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)),PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))), UserMajor.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_MAJOR))), cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_PHONE_NUMBER)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ACCOUNT_STATUS))));
                if (picIndex != -1 && !cursor.isNull(picIndex)) {
                    user.setProfilePicturePath(cursor.getString(picIndex));
                }
                userList.add(user);
            }
            return userList;
        } finally {
            cursor.close();
        }
    }

    public void changeStatus(long id, EntityStatus status){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(UserContract.COLUMN_ACCOUNT_STATUS,status.name());
        if (db.update(UserContract.TABLE_NAME,contentValues,UserContract.COLUMN_ID+" = ?",new String[]{String.valueOf(id)})==0)
            throw new RuntimeException("No user found with id " + id);
    }

    public User findByEmail(String email) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT P.*, U."+UserContract.COLUMN_MAJOR+", U."+UserContract.COLUMN_PHONE_NUMBER+", U."+UserContract.COLUMN_ACCOUNT_STATUS +
                " FROM " + UserContract.TABLE_NAME + " U" +
                " JOIN " + PersonContract.TABLE_NAME + " P" +
                " ON U." + UserContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID +
                " WHERE P." + PersonContract.COLUMN_EMAIL + " =?", new String[]{email});
        try {
            if(!cursor.moveToFirst()) return null;

            User user = new User(cursor.getLong(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)),
                    PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))),
                    UserMajor.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_MAJOR))),
                    cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_PHONE_NUMBER)),
                    EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ACCOUNT_STATUS))));
            int picIndex = cursor.getColumnIndex(PersonContract.COLUMN_PROFILE_PICTURE_PATH);
            if (picIndex != -1 && !cursor.isNull(picIndex)) {
                user.setProfilePicturePath(cursor.getString(picIndex));
            }
            return user;
        } finally {
            cursor.close();
        }
    }

    public List<User> search(String searchBy, boolean isAscending, String query) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        String aliasedSearchBy;
        if (searchBy.equals(UserContract.COLUMN_MAJOR) || searchBy.equals(UserContract.COLUMN_PHONE_NUMBER) || searchBy.equals(UserContract.COLUMN_ACCOUNT_STATUS))
            aliasedSearchBy = "U." + searchBy;
        else
            aliasedSearchBy = "P." + searchBy;

        Cursor cursor = db.rawQuery("SELECT P.*, U." + UserContract.COLUMN_MAJOR +
                ", U." + UserContract.COLUMN_PHONE_NUMBER +
                ", U." + UserContract.COLUMN_ACCOUNT_STATUS +
                " FROM " + UserContract.TABLE_NAME + " U " +
                " JOIN " + PersonContract.TABLE_NAME + " P ON U." + UserContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID +
                " WHERE " + aliasedSearchBy + " LIKE ? " +
                " ORDER BY " + aliasedSearchBy + " COLLATE NOCASE " + ((isAscending) ? "ASC" : "DESC"), new String[]{"%" + query + "%"});
        List<User> userList = new ArrayList<>();
        try {
            int picIndex = cursor.getColumnIndex(PersonContract.COLUMN_PROFILE_PICTURE_PATH);
            while (cursor.moveToNext()) {
                User user = new User(cursor.getLong(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)), PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))), UserMajor.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_MAJOR))), cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_PHONE_NUMBER)), EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.COLUMN_ACCOUNT_STATUS))));
                if (picIndex != -1 && !cursor.isNull(picIndex))
                    user.setProfilePicturePath(cursor.getString(picIndex));
                userList.add(user);
            }
            return userList;
        } finally{
            cursor.close();
        }
    }
}