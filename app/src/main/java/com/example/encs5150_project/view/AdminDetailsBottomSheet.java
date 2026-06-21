package com.example.encs5150_project.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminDetailsController;
import com.example.encs5150_project.controller.ImagePickerController;
import com.example.encs5150_project.controller.ImageUploadController;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.observer.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

public class AdminDetailsBottomSheet extends BottomSheetDialogFragment implements UploadStatus {

    private AdminDetailsController detailController;
    private ImageUploadController imageUploadController;
    private ImagePickerController imagePickerController;
    private Admin currentAdmin;
    private String uploadedProfilePicUrl = null;

    private TextInputEditText etFirstName, etLastName, etEmail, etSalary;
    private AutoCompleteTextView actvGender, actvRole;
    private TextView tvStatus, tvId, btnChangePhoto;
    private ShapeableImageView ivProfilePic;
    private ProgressBar progressBar;
    private LinearLayout llDefaultActions, llEditActions;
    private MaterialButton btnEditMode, btnAccountStatus, btnCancelEdit, btnSaveAdmin;
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageUploadController.handleImageSelected(requireContext(), selectedImage);
                }
            });

    public AdminDetailsBottomSheet() {}

    public void setSetupData(Admin admin, AdminDetailsController controller) {
        this.currentAdmin = admin;
        this.detailController = controller;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_admin_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etSalary = view.findViewById(R.id.etSalary);
        actvGender = view.findViewById(R.id.actvGender);
        actvRole = view.findViewById(R.id.actvRole);
        tvStatus = view.findViewById(R.id.tvDetailAccountStatus);
        tvId = view.findViewById(R.id.tvDetailId);
        ivProfilePic = view.findViewById(R.id.ivDetailProfilePic);
        progressBar = view.findViewById(R.id.progressBar);
        llDefaultActions = view.findViewById(R.id.llDefaultActions);
        llEditActions = view.findViewById(R.id.llEditActions);
        btnEditMode = view.findViewById(R.id.btnEditMode);
        btnAccountStatus = view.findViewById(R.id.btnAccountStatus);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnSaveAdmin = view.findViewById(R.id.btnSaveAdmin);
        btnChangePhoto = view.findViewById(R.id.tvChangePhotoAdmin);
        imageUploadController = new ImageUploadController(this);
        setupDropdowns();
        loadAdminData();
        btnEditMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable(true);
            }
        });
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAdminData();
                setEditable(false);
            }
        });
        btnAccountStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStatusToggle();
            }
        });
        btnSaveAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave();
            }
        });
        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoOptionsDialog();
            }
        });
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnChangePhoto.getVisibility() == View.VISIBLE) {
                    showPhotoOptionsDialog();
                }
            }
        });
        imagePickerController = new ImagePickerController(this, imageUri -> {
            imageUploadController.handleImageSelected(requireContext(), imageUri);
        });
    }

    private void setupDropdowns() {
        String[] genders = new String[PersonGender.values().length];
        for (int i = 0; i < PersonGender.values().length; i++) genders[i] = PersonGender.values()[i].name();
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(genderAdapter);

        String[] roles = new String[AdminRole.values().length];
        for (int i = 0; i < AdminRole.values().length; i++) roles[i] = AdminRole.values()[i].name();
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roles);
        actvRole.setAdapter(roleAdapter);
    }

    private void loadAdminData() {
        if (currentAdmin == null) return;
        etFirstName.setText(currentAdmin.getFirstName());
        etLastName.setText(currentAdmin.getLastName());
        etEmail.setText(currentAdmin.getEmail());
        etSalary.setText(String.valueOf(currentAdmin.getSalary()));
        actvGender.setText(currentAdmin.getGender().name(), false);
        actvRole.setText(currentAdmin.getRole().name(), false);
        tvId.setText("Admin ID: #" + currentAdmin.getId());
        boolean isEnabled = currentAdmin.getAccountStatus() == EntityStatus.ENABLED;
        tvStatus.setText("Status: " + currentAdmin.getAccountStatus().name());
        tvStatus.setTextColor(getResources().getColor(isEnabled ? R.color.success : R.color.error));
        int actionColor = ContextCompat.getColor(requireContext(), isEnabled ? R.color.error : R.color.success);
        btnAccountStatus.setText(isEnabled ? "Disable" : "Enable");
        btnAccountStatus.setTextColor(actionColor);
        btnAccountStatus.setStrokeColor(ColorStateList.valueOf(actionColor));
        uploadedProfilePicUrl = currentAdmin.getProfilePicturePath();
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(uploadedProfilePicUrl != null && !uploadedProfilePicUrl.isEmpty() ? uploadedProfilePicUrl : R.drawable.profile)
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(ivProfilePic);
    }

    private void setEditable(boolean isEditable) {
        etFirstName.setEnabled(isEditable);
        etLastName.setEnabled(isEditable);
        etEmail.setEnabled(isEditable);
        etSalary.setEnabled(isEditable);
        actvGender.setEnabled(isEditable);
        actvRole.setEnabled(isEditable);
        btnChangePhoto.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        llDefaultActions.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        llEditActions.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    private void handleSave() {
        AdminDetailsController.DetailResponse response = detailController.updateAdmin(currentAdmin, etFirstName.getText().toString(), etLastName.getText().toString(), etEmail.getText().toString(), etSalary.getText().toString(), actvGender.getText().toString(), actvRole.getText().toString(), uploadedProfilePicUrl);

        Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
        if (response.status() == AdminDetailsController.DetailStatus.SUCCESS) {
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshAccountList", result);
            setEditable(false);
            loadAdminData();
        }
    }

    private void handleStatusToggle() {
        AdminDetailsController.DetailResponse response = detailController.toggleAdminStatus(currentAdmin);
        if (response.status() == AdminDetailsController.DetailStatus.SUCCESS) {
            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshAccountList", result);
            loadAdminData();
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
        btnSaveAdmin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (!isAdded()) return;
        btnSaveAdmin.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onUploadSuccess(String imageUrl) {
        if (!isAdded()) return;
        hideProgress();
        uploadedProfilePicUrl = imageUrl;
        Glide.with(this).load(imageUrl).into(ivProfilePic);
        Toast.makeText(getContext(), "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}