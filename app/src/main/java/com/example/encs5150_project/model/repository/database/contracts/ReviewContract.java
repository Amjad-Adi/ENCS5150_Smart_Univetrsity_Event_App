package com.example.encs5150_project.model.repository.database.contracts;

public final class  ReviewContract {

    public static final String TABLE_NAME = "review";

    public static final String COLUMN_RESERVATION_ID = "reservation_id";
    public static final String COLUMN_RATING= "review_rating";
    public static final String COLUMN_TEXT = "review_text";
    public static final String COLUMN_DATE = "review_date";
    public static final String COLUMN_STATUS= "review_status";
    private ReviewContract() {
    }
}