package com.example.encs5150_project.model.repository.database.contracts;

public final class  AdminContract {

    public static final String TABLE_NAME = "admin";

    public static final String COLUMN_ID = "person_id";
    public static final String COLUMN_SALARY = "admin_salary";
    public static final String COLUMN_ACCOUNT_STATUS = "admin_account_status";
    public static final int DEFAULT_SALARY = 0;
    private AdminContract() {
    }
}