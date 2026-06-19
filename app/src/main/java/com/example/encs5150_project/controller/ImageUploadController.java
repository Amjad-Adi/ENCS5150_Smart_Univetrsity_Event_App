package com.example.encs5150_project.controller;

import android.content.Context;
import android.net.Uri;

import com.example.encs5150_project.model.ImageUploadModel;
import com.example.encs5150_project.model.observer.UploadCallbackObserver;
import com.example.encs5150_project.model.observer.UploadStatus;


public class ImageUploadController implements UploadCallbackObserver {

    private final ImageUploadModel model;
    private final UploadStatus uploadStatus;

    public ImageUploadController(UploadStatus view) {
        this.uploadStatus = view;
        this.model = new ImageUploadModel(this);
    }

    public void handleImageSelected(Context context, Uri imageUri) {
        if (imageUri == null) {
            uploadStatus.showError("No image selected.");
            return;
        }
        if (model.isFileTooLarge(context, imageUri)) {
            uploadStatus.showError("File is too large! Maximum allowed is 5MB.");
            return;
        }
        uploadStatus.showProgress();
        model.uploadToCloudinary(imageUri);
    }
    @Override
    public void onUploadSuccess(String imageUrl) {
        uploadStatus.hideProgress();
        uploadStatus.onUploadSuccess(imageUrl);
    }

    @Override
    public void onUploadError(String errorMessage) {
        uploadStatus.hideProgress();
        uploadStatus.showError("Upload failed: " + errorMessage);
    }
}