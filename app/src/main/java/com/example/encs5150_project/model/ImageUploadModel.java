package com.example.encs5150_project.model;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.encs5150_project.model.observer.UploadCallbackObserver;

import java.util.Map;

public class ImageUploadModel{
    private static final long MAX_FILE_SIZE = 5000000;//5MB
    private final UploadCallbackObserver observer;
    public ImageUploadModel(UploadCallbackObserver observer) {
        this.observer = observer;
    }
    public boolean isFileTooLarge(Context context, Uri uri) {
        long fileSize = 0;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (cursor.moveToFirst()) {
                fileSize = cursor.getLong(sizeIndex);
            }
            cursor.close();
        }
        return fileSize > MAX_FILE_SIZE;
    }

    public void uploadToCloudinary(Uri imageUri) {
        MediaManager.get().upload(imageUri)
                .unsigned("smart_campus_system")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String secureUrl = (String) resultData.get("secure_url");
                        if (observer != null) {
                            observer.onUploadSuccess(secureUrl);
                        }
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        observer.onUploadError(error.getDescription());
                    }
                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { }
                }).dispatch();
    }
}