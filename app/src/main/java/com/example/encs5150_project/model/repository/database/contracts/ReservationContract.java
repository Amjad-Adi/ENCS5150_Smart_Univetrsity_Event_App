package com.example.encs5150_project.model.repository.database.contracts;

public final class ReservationContract {

    public static final String TABLE_NAME = "reservation";

    public static final String COLUMN_ID = "reservation_id";

    public static final String COLUMN_USER_ID = "person_id";
    public static final String COLUMN_EVENT_ID = "event_id";

    public static final String COLUMN_TYPE = "reservation_type";
    public static final String COLUMN_STATUS = "reservation_status";
    public static final String COLUMN_ADDITIONAL_INFO = "reservation_additional_info";
    public static final String COLUMN_DATE = "reservation_date";
    public static final String COLUMN_PARTICIPATION_COUNT = "participation_count";
    public static final int DEFAULT_PARTICIPATION_COUNT = 1;
    public static final String DEFAULT_ADDITIONAL_INFO = "No additional info";
    private ReservationContract() {
    }
}