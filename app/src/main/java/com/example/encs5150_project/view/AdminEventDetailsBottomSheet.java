package com.example.encs5150_project.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminEventDetailsController;
import com.example.encs5150_project.controller.ImagePickerController;
import com.example.encs5150_project.controller.ImageUploadController;
import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.observer.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class AdminEventDetailsBottomSheet extends BottomSheetDialogFragment implements UploadStatus {

    private AdminEventDetailsController detailController;
    private ImageUploadController imageUploadController;
    private ImagePickerController imagePickerController;
    private Event currentEvent;
    private String uploadedEventPicUrl = null;
    private EventSummary eventSummary;
    private ShapeableImageView ivEventPic;
    private TextView tvChangePhoto, tvStatus, tvId;

    private TextInputEditText etTitle, etDescription, etCategory, etBookedSeats, etTotalSeats, etLocation, etDate, etTime;
    private LinearLayout llDefaultActions, llEditActions;
    private MaterialButton btnDisableEvent, btnEditMode, btnCancelEdit, btnSaveEvent;
    private ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        imageUploadController.handleImageSelected(requireContext(), selectedImage);
                    }
                }
            });

    public AdminEventDetailsBottomSheet() {}

    public void setSetupData(EventSummary summary, AdminEventDetailsController controller) {
        this.eventSummary = summary;
        this.currentEvent = summary.event();
        this.detailController = controller;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_admin_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivEventPic = view.findViewById(R.id.ivEventDetailPic);
        tvChangePhoto = view.findViewById(R.id.tvChangeEventPhoto);
        tvStatus = view.findViewById(R.id.tvDetailEventStatus);
        tvId = view.findViewById(R.id.tvDetailEventId);
        progressBar = view.findViewById(R.id.progressBar);
        etTitle = view.findViewById(R.id.etEventTitle);
        etDescription = view.findViewById(R.id.etEventDescription);
        etCategory = view.findViewById(R.id.etEventCategory);
        etBookedSeats = view.findViewById(R.id.etEventBookedSeats);
        etTotalSeats = view.findViewById(R.id.etEventTotalSeats);
        etLocation = view.findViewById(R.id.etEventLocation);
        etDate = view.findViewById(R.id.etEventDate);
        etTime = view.findViewById(R.id.etEventTime);
        llDefaultActions = view.findViewById(R.id.llDefaultActions);
        llEditActions = view.findViewById(R.id.llEditActions);
        btnDisableEvent = view.findViewById(R.id.btnDisableEvent);
        btnEditMode = view.findViewById(R.id.btnEditMode);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnSaveEvent = view.findViewById(R.id.btnSaveEvent);
        imageUploadController = new ImageUploadController(this);
        loadEventData();
        btnEditMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable(true);
            }
        });
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEventData();
                setEditable(false);
            }
        });
        btnDisableEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStatusToggle();
            }
        });
        btnSaveEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave();
            }
        });
        tvChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoOptionsDialog();
            }
        });
        ivEventPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvChangePhoto.getVisibility() == View.VISIBLE) {
                    showPhotoOptionsDialog();
                }
            }
        });
        imagePickerController = new ImagePickerController(this, imageUri -> {
            imageUploadController.handleImageSelected(requireContext(), imageUri);
        });
    }
    private void loadEventData() {
        if (eventSummary == null || currentEvent == null) return;
        etTitle.setText(currentEvent.getTitle());
        etDescription.setText(currentEvent.getDescription());
        etCategory.setText(currentEvent.getCategory());
        etBookedSeats.setText(String.valueOf(eventSummary.bookedSeats()));
        etTotalSeats.setText(String.valueOf(currentEvent.getTotalSeats()));
        etLocation.setText(currentEvent.getLocation());
        etDate.setText(currentEvent.getDate().toString());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
        etTime.setText(currentEvent.getTime().format(timeFormatter));
        tvId.setText("Event ID: #" + currentEvent.getId());
        tvStatus.setText("Status: ACTIVE");
        tvStatus.setTextColor(getResources().getColor(R.color.success));
        btnDisableEvent.setText("Disable");
        uploadedEventPicUrl = currentEvent.getImagePath();
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uploadedEventPicUrl != null && !uploadedEventPicUrl.isEmpty() ? uploadedEventPicUrl : R.drawable.events)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.events)
                .error(R.drawable.events)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivEventPic);
    }

    private void setEditable(boolean isEditable) {
        etTitle.setEnabled(isEditable);
        etDescription.setEnabled(isEditable);
        etCategory.setEnabled(isEditable);
        etTotalSeats.setEnabled(isEditable);
        etLocation.setEnabled(isEditable);
        etDate.setEnabled(isEditable);
        etTime.setEnabled(isEditable);
        tvChangePhoto.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        llDefaultActions.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        llEditActions.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    private void handleSave() {
        if (detailController != null && currentEvent != null) {
            try {
                String title = etTitle.getText().toString().trim();
                String desc = etDescription.getText().toString().trim();
                String category = etCategory.getText().toString().trim();
                String location = etLocation.getText().toString().trim();
                String dateStr = etDate.getText().toString().trim();
                String timeStr = etTime.getText().toString().trim();
                if (title.isEmpty() || dateStr.isEmpty() || location.isEmpty()) {
                    Toast.makeText(requireContext(), "Title, Date, and Location are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                int totalSeats = Integer.parseInt(etTotalSeats.getText().toString().trim());
                LocalDate parsedDate;
                LocalTime parsedTime;
                try {
                    parsedDate = LocalDate.parse(dateStr);
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
                    parsedTime = LocalTime.parse(timeStr, timeFormatter);
                } catch (DateTimeParseException e) {
                    Toast.makeText(requireContext(), "Invalid Date or Time format.", Toast.LENGTH_SHORT).show();
                    return;
                }
                AdminEventDetailsController.DetailResponse response = detailController.updateEvent(currentEvent, title, desc, category, totalSeats, location, parsedDate, parsedTime, uploadedEventPicUrl);
                if (response.status() == AdminEventDetailsController.DetailStatus.SUCCESS) {
                    Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                    Bundle result = new Bundle();
                    result.putBoolean("isUpdated", true);
                    getParentFragmentManager().setFragmentResult("RefreshEventList", result);
                    setEditable(false);
                    loadEventData();
                } else {
                    Toast.makeText(requireContext(), "Failed to update event details", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Total Seats must be a valid number", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleStatusToggle() {
        AdminEventDetailsController.DetailResponse response = detailController.toggleEventStatus(currentEvent);
        if (response.status() == AdminEventDetailsController.DetailStatus.SUCCESS) {
            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshEventList", result);
            loadEventData();
        } else {
            showError(response.message());
        }
    }

    private void showPhotoOptionsDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.image_picker, null);
        dialog.setContentView(sheetView);
        LinearLayout llCamera = sheetView.findViewById(R.id.llOptionCamera);
        LinearLayout llGallery = sheetView.findViewById(R.id.llOptionGallery);
        llCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imagePickerController.checkCameraPermissionAndLaunch();
            }
        });
        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                imagePickerController.launchGallery();
            }
        });
        dialog.show();
    }

    @Override
    public void showProgress() {
        if (!isAdded()) return;
        btnSaveEvent.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (!isAdded()) return;
        btnSaveEvent.setEnabled(true);
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUploadSuccess(String imageUrl) {
        if (!isAdded()) return;
        hideProgress();
        uploadedEventPicUrl = imageUrl;
        Glide.with(this).load(imageUrl).into(ivEventPic);
        Toast.makeText(getContext(), "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}