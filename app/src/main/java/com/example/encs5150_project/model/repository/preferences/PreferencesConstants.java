package com.example.encs5150_project.model.repository.preferences;


public final class PreferencesConstants {

    private PreferencesConstants() {
    }
    public static final String KEY_REMEMBER_ME = "remember_me";
    public static final String KEY_REMEMBERED_EMAIL = "remembered_email";
    public static final String KEY_SESSION_EMAIL = "session_email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public static final boolean DEFAULT_REMEMBER_ME = false;
    public static final String DEFAULT_REMEMBERED_EMAIL = "";
    public static final String DEFAULT_SESSION_EMAIL = "";
    public static final boolean DEFAULT_IS_LOGGED_IN = false;
}
