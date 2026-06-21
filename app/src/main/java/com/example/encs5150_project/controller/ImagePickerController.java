package com.example.encs5150_project.controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;
import com.example.encs5150_project.model.observer.ImagePickerObserver;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class ImagePickerController {

    private Uri cameraImageUri = null;
    private final Fragment fragment;
    private final ActivityResultLauncher<Uri> cameraLauncher;
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private final ActivityResultLauncher<Intent> galleryLauncher;
    public ImagePickerController(Fragment fragment, ImagePickerObserver listener) {
        this.fragment = fragment;

        // 1. Camera Capture Launcher
        this.cameraLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && cameraImageUri != null) {
                        listener.onImagePicked(cameraImageUri);
                    } else {
                        Toast.makeText(fragment.getContext(), "Camera capture cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
        this.requestCameraPermissionLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCameraInternal();
                    } else {
                        Toast.makeText(fragment.getContext(), "Camera permission is required to take a photo.", Toast.LENGTH_SHORT).show();
                    }
                });
        this.galleryLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        listener.onImagePicked(selectedImage);
                    }
                });
    }
    public void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCameraInternal();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    public void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void launchCameraInternal() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Profile Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Captured via Camera");

        cameraImageUri = fragment.requireContext().getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (cameraImageUri != null) {
            cameraLauncher.launch(cameraImageUri);
        } else {
            Toast.makeText(fragment.getContext(), "Error preparing camera.", Toast.LENGTH_SHORT).show();
        }
    }
}