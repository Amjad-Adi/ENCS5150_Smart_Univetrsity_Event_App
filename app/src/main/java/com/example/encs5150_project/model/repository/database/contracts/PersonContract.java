package com.example.encs5150_project.model.repository.database.contracts;

public final class PersonContract {
    public static final String TABLE_NAME = "person";

    public static final String COLUMN_ID = "person_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "person_email";
    public static final String COLUMN_PASSWORD = "person_password";
    public static final String COLUMN_GENDER = "person_gender";

    public static final String COLUMN_PROFILE_PICTURE_PATH = "user_profile_picture_path";
    public static final String DEFAULT_PROFILE_PICTURE_PATH = "https://drive.google.com/file/d/10bfgsTXE86sjxDPtQkXG9YwEno2CptlL/view?usp=drive_link";//Add this when possible
    private PersonContract() {
    }

}