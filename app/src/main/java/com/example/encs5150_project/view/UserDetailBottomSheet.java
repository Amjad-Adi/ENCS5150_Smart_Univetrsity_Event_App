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
import com.example.encs5150_project.controller.AdminUserDetailsController;
import com.example.encs5150_project.controller.ImagePickerController;
import com.example.encs5150_project.controller.ImageUploadController;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.observer.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

public class UserDetailBottomSheet extends BottomSheetDialogFragment implements UploadStatus {
    private AdminUserDetailsController detailController;
    private ImageUploadController imageUploadController;
    private ImagePickerController imagePickerController;
    private User currentUser;
    private String uploadedProfilePicUrl = null;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone;
    private AutoCompleteTextView actvGender, actvMajor;
    private TextView tvStatus, tvId,btnChangePhoto;
    private ShapeableImageView ivProfilePic;
    private ProgressBar progressBar;
    private LinearLayout llDefaultActions, llEditActions;
    private MaterialButton btnEditMode, btnAccountStatus, btnCancelEdit, btnSaveUser;
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageUploadController.handleImageSelected(requireContext(), selectedImage);
                }
            });

    public UserDetailBottomSheet() {}

    public void setSetupData(User user, AdminUserDetailsController controller) {
        this.currentUser = user;
        this.detailController = controller;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstName = view.findViewById(R.id.etFirstName);
        etLastName = view.findViewById(R.id.etLastName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        actvGender = view.findViewById(R.id.actvGender);
        actvMajor = view.findViewById(R.id.actvMajor);
        tvStatus = view.findViewById(R.id.tvDetailAccountStatus);
        tvId = view.findViewById(R.id.tvDetailId);
        ivProfilePic = view.findViewById(R.id.ivDetailProfilePic);
        progressBar = view.findViewById(R.id.progressBar);
        llDefaultActions = view.findViewById(R.id.llDefaultActions);
        llEditActions = view.findViewById(R.id.llEditActions);
        btnEditMode = view.findViewById(R.id.btnEditMode);
        btnAccountStatus = view.findViewById(R.id.btnAccountStatus);
        btnCancelEdit = view.findViewById(R.id.btnCancelEdit);
        btnSaveUser = view.findViewById(R.id.btnSaveUser);
        btnChangePhoto = view.findViewById(R.id.tvChangePhotoUser);
        imageUploadController = new ImageUploadController(this);
        setupDropdowns();
        loadUserData();
        btnEditMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditable(true);
            }
        });
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUserData();
                setEditable(false);
            }
        });
        btnAccountStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleStatusToggle();
            }});
        btnSaveUser.setOnClickListener(new View.OnClickListener() {
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
        String[] majors = new String[UserMajor.values().length];
        for (int i = 0; i < UserMajor.values().length; i++) majors[i] = UserMajor.values()[i].name();
        ArrayAdapter<String> majorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, majors);
        actvMajor.setAdapter(majorAdapter);
    }

    private void loadUserData() {
        if (currentUser == null) return;
        etFirstName.setText(currentUser.getFirstName());
        etLastName.setText(currentUser.getLastName());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhoneNumber());
        actvGender.setText(currentUser.getGender().name(), false);
        actvMajor.setText(currentUser.getMajor().name(), false);
        tvId.setText("User ID: #" + currentUser.getId());
        boolean isEnabled = currentUser.getAccountStatus() == EntityStatus.ENABLED;
        tvStatus.setText("Status: " + currentUser.getAccountStatus().name());
        int statusColor = ContextCompat.getColor(requireContext(), isEnabled ? R.color.success : R.color.error);
        tvStatus.setTextColor(statusColor);
        int actionColor = ContextCompat.getColor(requireContext(), isEnabled ? R.color.error : R.color.success);
        btnAccountStatus.setText(isEnabled ? "Disable" : "Enable");
        btnAccountStatus.setTextColor(actionColor);
        btnAccountStatus.setStrokeColor(ColorStateList.valueOf(actionColor));
        uploadedProfilePicUrl = currentUser.getProfilePicturePath();
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
        etPhone.setEnabled(isEditable);
        actvGender.setEnabled(isEditable);
        actvMajor.setEnabled(isEditable);
        btnChangePhoto.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        llDefaultActions.setVisibility(isEditable ? View.GONE : View.VISIBLE);
        llEditActions.setVisibility(isEditable ? View.VISIBLE : View.GONE);
    }

    private void handleSave() {
        AdminUserDetailsController.DetailResponse response = detailController.updateUser(currentUser, etFirstName.getText().toString(), etLastName.getText().toString(), etEmail.getText().toString(), etPhone.getText().toString(), actvGender.getText().toString(), actvMajor.getText().toString(), uploadedProfilePicUrl);
        Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
        if (response.status() == AdminUserDetailsController.DetailStatus.SUCCESS) {
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshAccountList", result);
            setEditable(false);
            loadUserData();
        }
    }

    private void handleStatusToggle() {
        AdminUserDetailsController.DetailResponse response = detailController.toggleUserStatus(currentUser);
        if (response.status() == AdminUserDetailsController.DetailStatus.SUCCESS) {
            Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshAccountList", result);//Check
            loadUserData();
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
        btnSaveUser.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (!isAdded()) return;
        btnSaveUser.setEnabled(true);
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