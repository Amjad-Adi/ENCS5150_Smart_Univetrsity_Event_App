package com.example.encs5150_project.model.repository.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.ReservationStatus;
import com.example.encs5150_project.model.entity.ReservationType;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.database.contracts.AdminContract;
import com.example.encs5150_project.model.repository.database.contracts.EventContract;
import com.example.encs5150_project.model.repository.database.contracts.FavouriteContract;
import com.example.encs5150_project.model.repository.database.contracts.PersonContract;
import com.example.encs5150_project.model.repository.database.contracts.ReservationContract;
import com.example.encs5150_project.model.repository.database.contracts.ReviewContract;
import com.example.encs5150_project.model.repository.database.contracts.UserContract;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper instance;
    private static final String DATABASE_NAME = "smart_university_events.db";
    private static final int DATABASE_VERSION = 1;
    private DataBaseHelper(@Nullable Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); //To enforce foreign keys
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        createEventTableSQL(db);
        createPersonTableSQL(db);
        createUserTableSQL(db);
        createAdminTableSQL(db);
        createReservationTableSQL(db);
        createFavouriteTableSQL(db);
        createReviewTableSQL(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public static DataBaseHelper getInstance(@Nullable Context context){
        if(instance==null)
            instance=new DataBaseHelper(context);
        return instance;
    }
    private void createEventTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(EventContract.TABLE_NAME).append(" ( ")
                .append(EventContract.COLUMN_ID).append(" INTEGER PRIMARY KEY, ")
                .append(EventContract.COLUMN_TITLE).append(" TEXT NOT NULL, ")
                .append(EventContract.COLUMN_DESCRIPTION).append(" TEXT DEFAULT '").append(EventContract.DEFAULT_DESCRIPTION).append("', ")
                .append(EventContract.COLUMN_CATEGORY).append(" TEXT DEFAULT '").append(EventContract.DEFAULT_CATEGORY).append("', ")
                .append(EventContract.COLUMN_DATE).append(" TEXT NOT NULL, ")//SQLite doesn't have DATE type
                .append(EventContract.COLUMN_TIME).append(" TEXT NOT NULL, ")//SQLite neither have TIME type
                .append(EventContract.COLUMN_LOCATION).append(" TEXT NOT NULL, ")
                .append(EventContract.COLUMN_TOTAL_SEATS).append(" INTEGER NOT NULL CHECK(").append(EventContract.COLUMN_TOTAL_SEATS).append(" > 0), ")
                .append(EventContract.COLUMN_IMAGE_PATH).append(" TEXT DEFAULT '").append(EventContract.DEFAULT_IMAGE_PATH).append("', ")
                .append(EventContract.COLUMN_STATUS).append(" TEXT NOT NULL DEFAULT '").append(EntityStatus.ENABLED.name()).append("' CHECK(").append(EventContract.COLUMN_STATUS).append(" IN ('").append(EntityStatus.ENABLED.name()).append("', '").append(EntityStatus.DISABLED.name()).append("')))").toString());
    }
    private void createPersonTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(PersonContract.TABLE_NAME).append(" ( ")
                .append(PersonContract.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(PersonContract.COLUMN_FIRST_NAME).append(" TEXT NOT NULL, ")
                .append(PersonContract.COLUMN_LAST_NAME).append(" TEXT NOT NULL, ")
                .append(PersonContract.COLUMN_EMAIL).append(" TEXT NOT NULL UNIQUE, ")
                .append(PersonContract.COLUMN_PASSWORD).append(" TEXT NOT NULL, ")
                .append(PersonContract.COLUMN_GENDER).append(" TEXT NOT NULL CHECK(").append(PersonContract.COLUMN_GENDER).append(" IN ('").append(PersonGender.Male.name()).append("', '").append(PersonGender.Female.name()).append("')))").toString());
    }
    private void createUserTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(UserContract.TABLE_NAME).append(" ( ")
                .append(UserContract.COLUMN_ID).append(" INTEGER PRIMARY KEY, ")
                .append(UserContract.COLUMN_MAJOR).append(" TEXT DEFAULT '").append(UserContract.DEFAULT_MAJOR).append("' CHECK(").append(UserContract.COLUMN_MAJOR).append(" IN ('").append(UserMajor.ENGINEERING.name()).append("', '").append(UserMajor.BUSINESS.name()).append("', '").append(UserMajor.MEDICAL_FIELDS.name()).append("', '").append(UserMajor.HUMANITIES.name()).append("', '").append(UserMajor.SCIENCES.name()).append("', '").append(UserMajor.LAW_AND_POLITICS.name()).append("', '").append(UserMajor.ARTS_AND_DESIGN.name()).append("', '").append(UserMajor.COMPUTING.name()).append("', '").append(UserMajor.EDUCATION.name()).append("', '").append(UserMajor.MEDIA_AND_COMMUNICATION.name()).append("', '").append(UserMajor.OTHER.name()).append("')), ")
                .append(UserContract.COLUMN_PHONE_NUMBER).append(" TEXT NOT NULL UNIQUE, ")
                .append(UserContract.COLUMN_PROFILE_PICTURE_PATH).append(" TEXT DEFAULT '").append(UserContract.DEFAULT_PROFILE_PICTURE_PATH).append("', ")
                .append(UserContract.COLUMN_ACCOUNT_STATUS).append(" TEXT NOT NULL DEFAULT '").append(EntityStatus.ENABLED.name()).append("' CHECK(").append(UserContract.COLUMN_ACCOUNT_STATUS).append(" IN ('").append(EntityStatus.ENABLED.name()).append("', '").append(EntityStatus.DISABLED.name()).append("')), ")
                .append("FOREIGN KEY (").append(UserContract.COLUMN_ID).append(") REFERENCES ").append(PersonContract.TABLE_NAME).append("(").append(PersonContract.COLUMN_ID).append(") ON DELETE CASCADE ON UPDATE CASCADE)").toString());
    }
    private void createAdminTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(AdminContract.TABLE_NAME).append(" ( ")
                .append(AdminContract.COLUMN_ID).append(" INTEGER PRIMARY KEY, ")
                .append(AdminContract.COLUMN_SALARY).append(" REAL DEFAULT ").append(AdminContract.DEFAULT_SALARY).append(" CHECK(").append(AdminContract.COLUMN_SALARY).append(" > 0), ")
                .append(AdminContract.COLUMN_ROLE).append(" TEXT NOT NULL DEFAULT '").append(AdminContract.DEFAULT_ROLE).append("' CHECK(").append(AdminContract.COLUMN_ROLE).append(" IN ('").append(AdminRole.EMPLOYEE.name()).append("', '").append(AdminRole.ADMINISTRATOR.name()).append("')), ")
                .append(AdminContract.COLUMN_ACCOUNT_STATUS).append(" TEXT NOT NULL DEFAULT '").append(EntityStatus.ENABLED.name()).append("' CHECK(").append(AdminContract.COLUMN_ACCOUNT_STATUS).append(" IN ('").append(EntityStatus.ENABLED.name()).append("', '").append(EntityStatus.DISABLED.name()).append("')), ")
                .append("FOREIGN KEY (").append(AdminContract.COLUMN_ID).append(") REFERENCES ").append(PersonContract.TABLE_NAME).append("(").append(PersonContract.COLUMN_ID).append(") ON DELETE CASCADE ON UPDATE CASCADE)").toString());
    }
    private void createReservationTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(ReservationContract.TABLE_NAME).append(" ( ")
                .append(ReservationContract.COLUMN_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ReservationContract.COLUMN_EVENT_ID).append(" INTEGER NOT NULL, ")
                .append(ReservationContract.COLUMN_USER_ID).append(" INTEGER NOT NULL, ")
                .append(ReservationContract.COLUMN_DATE).append(" TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, ")
                .append(ReservationContract.COLUMN_PARTICIPATION_COUNT).append(" INTEGER NOT NULL DEFAULT ").append(ReservationContract.DEFAULT_PARTICIPATION_COUNT).append(" CHECK(").append(ReservationContract.COLUMN_PARTICIPATION_COUNT).append(" > 0), ")
                .append(ReservationContract.COLUMN_ADDITIONAL_INFO).append(" TEXT DEFAULT '").append(ReservationContract.DEFAULT_ADDITIONAL_INFO).append("', ")
                .append(ReservationContract.COLUMN_TYPE).append(" TEXT NOT NULL DEFAULT '").append(ReservationType.REGULAR).append("' CHECK(").append(ReservationContract.COLUMN_TYPE).append(" IN ('").append(ReservationType.REGULAR.name()).append("', '").append(ReservationType.VIP.name()).append("')), ")
                .append(ReservationContract.COLUMN_STATUS).append(" TEXT NOT NULL DEFAULT '").append(ReservationStatus.CONFIRMED).append("' CHECK(").append(ReservationContract.COLUMN_STATUS).append(" IN ('").append(ReservationStatus.CONFIRMED.name()).append("', '").append(ReservationStatus.CANCELED_BY_ADMIN.name()).append("', '").append(ReservationStatus.DELETED_BY_USER.name()).append("', '").append(ReservationStatus.COMPLETED.name()).append("')), ")
                .append("FOREIGN KEY (").append(ReservationContract.COLUMN_EVENT_ID).append(") REFERENCES ").append(EventContract.TABLE_NAME).append("(").append(EventContract.COLUMN_ID).append(") ON DELETE CASCADE ON UPDATE CASCADE, ")
                .append("FOREIGN KEY (").append(ReservationContract.COLUMN_USER_ID).append(") REFERENCES ").append(UserContract.TABLE_NAME).append("(").append(UserContract.COLUMN_ID).append(") ON UPDATE CASCADE, ")
                .append("UNIQUE (").append(ReservationContract.COLUMN_USER_ID).append(", ").append(ReservationContract.COLUMN_EVENT_ID).append("))").toString());
    }
    private void createFavouriteTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(FavouriteContract.TABLE_NAME).append(" ( ")
                .append(FavouriteContract.COLUMN_EVENT_ID).append(" INTEGER NOT NULL, ")
                .append(FavouriteContract.COLUMN_USER_ID).append(" INTEGER NOT NULL, ")
                .append(FavouriteContract.COLUMN_DATE).append(" TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, ")
                .append("PRIMARY KEY(").append(FavouriteContract.COLUMN_EVENT_ID).append(", ").append(FavouriteContract.COLUMN_USER_ID).append("), ")
                .append("FOREIGN KEY (").append(FavouriteContract.COLUMN_EVENT_ID).append(") REFERENCES ").append(EventContract.TABLE_NAME).append("(").append(EventContract.COLUMN_ID).append(") ON DELETE CASCADE ON UPDATE CASCADE, ")
                .append("FOREIGN KEY (").append(FavouriteContract.COLUMN_USER_ID).append(") REFERENCES ").append(UserContract.TABLE_NAME).append("(").append(UserContract.COLUMN_ID).append(")  ON DELETE CASCADE ON UPDATE CASCADE)").toString());
    }
    private void createReviewTableSQL(SQLiteDatabase db){
        StringBuilder sqlStringBuilder=new StringBuilder();
        db.execSQL(
                sqlStringBuilder.append("CREATE TABLE ").append(ReviewContract.TABLE_NAME).append(" ( ")
                .append(ReviewContract.COLUMN_RESERVATION_ID).append(" INTEGER PRIMARY KEY, ")
                .append(ReviewContract.COLUMN_RATING).append(" REAL NOT NULL CHECK(").append(ReviewContract.COLUMN_RATING).append(" BETWEEN 1 AND 5), ")
                .append(ReviewContract.COLUMN_TEXT).append(" TEXT, ")
                .append(ReviewContract.COLUMN_DATE).append(" TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, ")
                .append(ReviewContract.COLUMN_STATUS).append(" TEXT NOT NULL DEFAULT '").append(EntityStatus.ENABLED.name()).append("' CHECK(").append(ReviewContract.COLUMN_STATUS).append(" IN ('").append(EntityStatus.ENABLED.name()).append("', '").append(EntityStatus.DISABLED.name()).append("')), ")
                .append("FOREIGN KEY (").append(ReviewContract.COLUMN_RESERVATION_ID).append(") REFERENCES ").append(ReservationContract.TABLE_NAME).append("(").append(ReservationContract.COLUMN_ID).append(")  ON DELETE CASCADE ON UPDATE CASCADE)").toString());
    }
}