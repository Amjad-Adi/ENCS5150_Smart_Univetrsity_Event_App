package com.example.encs5150_project.model.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.*;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.database.contracts.*;

import java.util.ArrayList;
import java.util.List;

public class AdminRepository {
    private final DataBaseHelper dataBaseHelper;
    private final PersonRepository personRepository;
    public AdminRepository(DataBaseHelper dataBaseHelper){
        this.dataBaseHelper=dataBaseHelper;
        personRepository=new PersonRepository();
    }

    public void insert(Admin admin) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            personRepository.insert(db,admin);
            ContentValues contentValues = new ContentValues();
            contentValues.put(AdminContract.COLUMN_ID, admin.getId());
            contentValues.put(AdminContract.COLUMN_SALARY, admin.getSalary());
            contentValues.put(AdminContract.COLUMN_ROLE, admin.getRole().toString());
            if(db.insert(AdminContract.TABLE_NAME,null,contentValues)==-1)
                throw new RuntimeException("Failed to insert admin into SQLite.");
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void update(Admin admin) {
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        db.beginTransaction();
        try{
            personRepository.update(db,admin);
            ContentValues adminValues=new ContentValues();
            adminValues.put(AdminContract.COLUMN_SALARY, admin.getSalary());
            adminValues.put(AdminContract.COLUMN_ROLE, admin.getRole().toString());
            if(db.update(AdminContract.TABLE_NAME,adminValues,AdminContract.COLUMN_ID+" = ?",new String[]{String.valueOf(admin.getId())})==0)
                throw new RuntimeException("No admin found with id " + admin.getId());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Admin findById(long id) {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT P.*, A."+AdminContract.COLUMN_SALARY+", A."+AdminContract.COLUMN_ACCOUNT_STATUS +
                    " FROM " + AdminContract.TABLE_NAME + " A" +
                    " JOIN " + PersonContract.TABLE_NAME + " P" +
                    " ON A." + AdminContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID +
                    " WHERE A." + AdminContract.COLUMN_ID + " = ?",new String[]{String.valueOf(id)});
        try{
            if(!cursor.moveToFirst())
                return null;
            return new Admin(cursor.getLong(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)), PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))), cursor.getDouble(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_SALARY)),AdminRole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ROLE))) ,EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ACCOUNT_STATUS))));
        }finally {
            cursor.close();
        }
    }

    public List<Admin> findAll() {
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                    "SELECT P.*, A."+AdminContract.COLUMN_SALARY+", A."+AdminContract.COLUMN_ACCOUNT_STATUS +
                        " FROM " + AdminContract.TABLE_NAME + " A " +
                        " JOIN " + PersonContract.TABLE_NAME + " P " +
                        " ON A." + AdminContract.COLUMN_ID + " = P." + PersonContract.COLUMN_ID,null);
        List<Admin>adminList=new ArrayList<>();
        try {
            while(cursor.moveToNext())
                adminList.add(new Admin(cursor.getLong(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ID)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_FIRST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_LAST_NAME)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_EMAIL)), cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_PASSWORD)), PersonGender.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(PersonContract.COLUMN_GENDER))), cursor.getDouble(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_SALARY)), AdminRole.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ROLE))),EntityStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(AdminContract.COLUMN_ACCOUNT_STATUS)))));
            return adminList;
        } finally {
            cursor.close();
        }
    }
    public void changeStatus(long id, EntityStatus status){
        SQLiteDatabase db = dataBaseHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AdminContract.COLUMN_ACCOUNT_STATUS,status.name());
        if (db.update(AdminContract.TABLE_NAME,contentValues,AdminContract.COLUMN_ID+" = ?",new String[]{String.valueOf(id)})==0)
            throw new RuntimeException("No admin found with id " + id);
    }
}
