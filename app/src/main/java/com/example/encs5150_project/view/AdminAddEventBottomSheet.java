package com.example.encs5150_project.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminAddEventController;
import com.example.encs5150_project.controller.ImagePickerController;
import com.example.encs5150_project.controller.ImageUploadController;
import com.example.encs5150_project.model.observer.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class AdminAddEventBottomSheet extends BottomSheetDialogFragment implements UploadStatus {

    private AdminAddEventController controller;
    private ImageUploadController imageUploadController;
    private ImagePickerController imagePickerController;
    private String uploadedEventPicUrl = null;
    private ShapeableImageView ivEventPic;
    private MaterialButton btnSubmit;
    private TextInputEditText etTitle, etDescription, etCategory, etTotalSeats, etLocation, etDate, etTime;

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

    public AdminAddEventBottomSheet() {}

    public void setSetupData(AdminAddEventController controller) {
        this.controller = controller;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_admin_add_event, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivEventPic = view.findViewById(R.id.ivAddEventPic);
        etTitle = view.findViewById(R.id.etAddEventTitle);
        etDescription = view.findViewById(R.id.etAddEventDescription);
        etCategory = view.findViewById(R.id.etAddEventCategory);
        etTotalSeats = view.findViewById(R.id.etAddEventTotalSeats);
        etLocation = view.findViewById(R.id.etAddEventLocation);
        etDate = view.findViewById(R.id.etAddEventDate);
        etTime = view.findViewById(R.id.etAddEventTime);
        btnSubmit = view.findViewById(R.id.btnSubmitEvent);
        imageUploadController = new ImageUploadController(this);
        imagePickerController = new ImagePickerController(this, imageUri -> {
            imageUploadController.handleImageSelected(requireContext(), imageUri);
        });
        setupDateTimePickers();
        view.findViewById(R.id.tvAddEventPhoto).setOnClickListener(v -> showPhotoOptionsDialog());
        ivEventPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoOptionsDialog();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    private void setupDateTimePickers() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDate.setText(selectedDate);
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                    String amPm = hourOfDay >= 12 ? "PM" : "AM";
                    int displayHour = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                    String selectedTime = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, amPm);
                    etTime.setText(selectedTime);
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
            }
        });
    }

    private void handleSubmit() {
        if (controller == null) return;
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String dateStr = etDate.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();
        String totalSeatsRaw = etTotalSeats.getText().toString().trim();
        if (title.isEmpty() || dateStr.isEmpty() || location.isEmpty() || totalSeatsRaw.isEmpty()) {
            Toast.makeText(requireContext(), "Title, Seats, Date, and Location are required", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int totalSeats = Integer.parseInt(totalSeatsRaw);
            LocalDate parsedDate = LocalDate.parse(dateStr);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
            LocalTime parsedTime = LocalTime.parse(timeStr, timeFormatter);
            AdminAddEventController.AddResponse response = controller.addEvent(title, desc, category, totalSeats, location, parsedDate, parsedTime, uploadedEventPicUrl);
            Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
            if (response.status() == AdminAddEventController.AddStatus.SUCCESS) {
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("RefreshEventList", result);
                dismiss();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Total Seats must be a valid positive number", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid Date or Time selection formatting rules", Toast.LENGTH_SHORT).show();
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
        btnSubmit.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        if (!isAdded()) return;
        btnSubmit.setEnabled(true);
    }

    @Override
    public void onUploadSuccess(String imageUrl) {
        if (!isAdded()) return;
        hideProgress();
        uploadedEventPicUrl = imageUrl;
        Glide.with(this).load(imageUrl).into(ivEventPic);
        Toast.makeText(getContext(), "Image selected successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}