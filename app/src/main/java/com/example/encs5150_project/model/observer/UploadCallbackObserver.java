package com.example.encs5150_project.model.observer;
public interface UploadCallbackObserver {
    void onUploadSuccess(String imageUrl);
    void onUploadError(String errorMessage);
}