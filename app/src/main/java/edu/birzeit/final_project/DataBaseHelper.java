package edu.birzeit.final_project;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE USER(" +
                        "EMAIL TEXT PRIMARY KEY, " +
                        "FIRST_NAME TEXT, " +
                        "LAST_NAME TEXT, " +
                        "PASSWORD TEXT, " +
                        "GENDER TEXT, " +
                        "CATEGORY TEXT, " +
                        "PHONE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS USER");
        onCreate(sqLiteDatabase);
    }

    // Add a new user (used in registration)
    public void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL", user.getmEmail());
        contentValues.put("FIRST_NAME", user.getmFirstName());
        contentValues.put("LAST_NAME", user.getmLastName());
        contentValues.put("PASSWORD", user.getmPassword());
        contentValues.put("GENDER", user.getmGender());
        contentValues.put("CATEGORY", user.getmCategory());
        contentValues.put("PHONE", user.getmPhone());
        sqLiteDatabase.insert("USER", null, contentValues);
    }

    // Check whether an email already exists (used in registration)
    public boolean isEmailExists(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT EMAIL FROM USER WHERE EMAIL = ?", new String[]{email});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get a single user by email (used in login)
    public User getUserByEmail(String email) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT * FROM USER WHERE EMAIL = ?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setmEmail(cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")));
            user.setmFirstName(cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")));
            user.setmLastName(cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")));
            user.setmPassword(cursor.getString(cursor.getColumnIndexOrThrow("PASSWORD")));
            user.setmGender(cursor.getString(cursor.getColumnIndexOrThrow("GENDER")));
            user.setmCategory(cursor.getString(cursor.getColumnIndexOrThrow("CATEGORY")));
            user.setmPhone(cursor.getString(cursor.getColumnIndexOrThrow("PHONE")));
        }
        cursor.close();
        return user;
    }
}