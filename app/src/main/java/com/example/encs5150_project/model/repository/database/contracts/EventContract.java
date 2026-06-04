package com.example.encs5150_project.model.repository.database.contracts;

public final class EventContract {
    public static final String TABLE_NAME="event";
    public static final String COLUMN_ID="event_id";
    public static final String COLUMN_TITLE="event_title";
    public static final String COLUMN_DESCRIPTION="event_description";
    public static final String COLUMN_CATEGORY="event_category";
    public static final String COLUMN_DATE="event_date";
    public static final String COLUMN_TIME="event_time";
    public static final String COLUMN_LOCATION ="event_location";
    public static final String COLUMN_TOTAL_SEATS="event_total_seats";
    public static final String COLUMN_IMAGE_PATH="event_image_path";
    public static final String COLUMN_STATUS="event_status";
    public static final String DEFAULT_DESCRIPTION="No description";
    public static final String DEFAULT_CATEGORY="No category";
    public static final String DEFAULT_IMAGE_PATH="No image path";
    private EventContract(){}

}
