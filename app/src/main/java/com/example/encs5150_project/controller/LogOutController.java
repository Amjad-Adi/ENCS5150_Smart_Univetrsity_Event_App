package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class LogOutController {
    private final SharedPrefManager sharedPrefManager;
    public LogOutController(SharedPrefManager sharedPrefManager){
        this.sharedPrefManager = sharedPrefManager;
    }
    public void logout() {
        sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, null);
    }
}
