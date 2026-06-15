package com.example.encs5150_project.model.repository.database.contracts;

import com.example.encs5150_project.model.entity.AdminRole;

public final class  AdminContract {

    public static final String TABLE_NAME = "admin";

    public static final String COLUMN_ID = "person_id";
    public static final String COLUMN_SALARY = "admin_salary";
    public static final String COLUMN_ROLE = "admin_role";
    public static final String COLUMN_ACCOUNT_STATUS = "admin_account_status";
    public static final int DEFAULT_SALARY = 0;
    public static final String DEFAULT_ROLE = AdminRole.EMPLOYEE.name();
    private AdminContract() {
    }
}