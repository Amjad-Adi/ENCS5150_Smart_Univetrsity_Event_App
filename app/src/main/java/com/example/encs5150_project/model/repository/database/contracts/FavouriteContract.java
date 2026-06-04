package com.example.encs5150_project.model.repository.database.contracts;
public final class  FavouriteContract {

    public static final String TABLE_NAME = "Favourite";
    public static final String COLUMN_USER_ID = "favourite_user_id";
    public static final String COLUMN_EVENT_ID = "favourite_event_id";
    public static final String COLUMN_DATE = "favourite_date";

    private FavouriteContract() {
    }
}