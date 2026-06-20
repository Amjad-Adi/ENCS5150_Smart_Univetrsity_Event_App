package com.example.encs5150_project.model.config;

import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;

public final class DefaultAdmin {
    private DefaultAdmin() {}

    public static final String FIRST_NAME = "Admin";
    public static final String LAST_NAME = "User";
    public static final String EMAIL = "admin@admin.com";
    public static final String HASHED_PASSWORD = "310000:rHrRBAzfFoTWw188FwKYmw==:j9QcaVQd6FS2VcRpcgIL9fioCZ6Bx7W9TngdvhWPrzU=";
    public static final PersonGender GENDER = PersonGender.MALE;
    public static final double SALARY = 5000.0;
    public static final AdminRole ROLE = AdminRole.ADMINISTRATOR;
    public static final EntityStatus ACCOUNT_STATUS = EntityStatus.ENABLED;
}