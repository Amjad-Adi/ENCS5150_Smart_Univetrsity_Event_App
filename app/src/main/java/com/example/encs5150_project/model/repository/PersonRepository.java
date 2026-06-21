package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.Person;
import com.example.encs5150_project.model.repository.database.contracts.PersonContract;

public class PersonRepository {
    public void insert(SQLiteDatabase db, Person person) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonContract.COLUMN_FIRST_NAME, person.getFirstName());
        contentValues.put(PersonContract.COLUMN_LAST_NAME, person.getLastName());
        contentValues.put(PersonContract.COLUMN_EMAIL, person.getEmail());
        contentValues.put(PersonContract.COLUMN_PASSWORD, person.getPassword());
        contentValues.put(PersonContract.COLUMN_GENDER, person.getGender().name());
        long generatedId = db.insert(PersonContract.TABLE_NAME, null, contentValues);
        if (generatedId == -1)
            throw new RuntimeException("Failed to insert person into SQLite.");
        person.setId(generatedId);
    }

    public void update(SQLiteDatabase db, Person person) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PersonContract.COLUMN_FIRST_NAME, person.getFirstName());
        contentValues.put(PersonContract.COLUMN_LAST_NAME, person.getLastName());
        contentValues.put(PersonContract.COLUMN_EMAIL, person.getEmail());
        contentValues.put(PersonContract.COLUMN_PASSWORD, person.getPassword());
        contentValues.put(PersonContract.COLUMN_GENDER, person.getGender().name());
        contentValues.put(PersonContract.COLUMN_PROFILE_PICTURE_PATH, person.getProfilePicturePath());
        if (db.update(PersonContract.TABLE_NAME, contentValues, PersonContract.COLUMN_ID + " = ?", new String[]{String.valueOf(person.getId())}) == 0)
            throw new RuntimeException("No person found with id " + person.getId());
    }
    public boolean isEmailExists(SQLiteDatabase db,String email) {
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + PersonContract.TABLE_NAME +
                " WHERE " + PersonContract.COLUMN_EMAIL + " =?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}