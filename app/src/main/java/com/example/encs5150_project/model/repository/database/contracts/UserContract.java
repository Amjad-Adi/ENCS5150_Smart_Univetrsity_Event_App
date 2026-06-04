package com.example.encs5150_project.model.repository.database.contracts;

public final class UserContract {
        public static final String TABLE_NAME = "user";

        public static final String COLUMN_ID = "person_id";
        public static final String COLUMN_MAJOR = "user_major";
        public static final String COLUMN_PHONE_NUMBER = "user_phone_number";
        public static final String COLUMN_PROFILE_PICTURE_PATH = "user_profile_picture_path";
        public static final String COLUMN_ACCOUNT_STATUS = "user_account_status";
        public static final String DEFAULT_MAJOR = "No major";
        public static final String DEFAULT_PROFILE_PICTURE_PATH = "Default_Picture_URL";//Add this when possible
        private UserContract() {
        }
}