package com.example.encs5150_project.model.repository.config;

import com.example.encs5150_project.model.entity.AdminRole;

public final class AdminCredentials {
    private AdminCredentials(){}
    public static final String EMAIL = "admin@admin.com";
    public static final String PASSWORD = "Admin123!";
    public static final AdminRole role=AdminRole.ADMINISTRATOR;
}
