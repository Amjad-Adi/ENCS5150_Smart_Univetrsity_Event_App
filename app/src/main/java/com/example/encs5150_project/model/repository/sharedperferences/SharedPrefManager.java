package com.example.encs5150_project.model.repository.sharedperferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager{
    private final static String appPreferences="preferences";
    private static SharedPrefManager instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private SharedPrefManager(Context context) {
        sharedPreferences=context.getSharedPreferences(appPreferences,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }
    public static SharedPrefManager getInstance(Context context){
        if(instance==null)
            instance=new SharedPrefManager(context);
        return instance;
    }
    public String readString(String key, String defaultValue){
        return sharedPreferences.getString(key,defaultValue);
    }
    public boolean writeString(String key, String value){
        editor.putString(key,value);
        return editor.commit();
    }
    public Boolean readBoolean(String key, Boolean defaultValue){
        return sharedPreferences.getBoolean(key,defaultValue);
    }
    public boolean writeBoolean(String key, Boolean value){
        editor.putBoolean(key,value);
        return editor.commit();
    }
}
