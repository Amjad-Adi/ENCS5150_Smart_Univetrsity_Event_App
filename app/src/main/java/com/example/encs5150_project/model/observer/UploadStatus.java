package com.example.encs5150_project.model.observer;

public interface UploadStatus {
    void showProgress();
    void hideProgress();
    void onUploadSuccess(String url);
    void showError(String message);
}
