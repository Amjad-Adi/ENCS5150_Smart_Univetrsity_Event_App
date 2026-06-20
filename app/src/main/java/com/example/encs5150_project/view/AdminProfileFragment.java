package com.example.encs5150_project.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AdminProfileController;
import com.example.encs5150_project.controller.ImageUploadController;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.observer.UploadStatus;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

public class AdminProfileFragment extends Fragment implements UploadStatus {

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    private AutoCompleteTextView actvGender;
    private ShapeableImageView ivProfilePic;
    private MaterialButton btnSave, btnLogout;

    private AdminProfileController profileController;
    private ImageUploadController imageUploadController;
    private Admin currentAdmin;
    private String uploadedProfilePicUrl = null;
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageUploadController.handleImageSelected(requireContext(), selectedImage);
                }
            });

    public AdminProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstName = view.findViewById(R.id.etProfileFirstName);
        etLastName = view.findViewById(R.id.etProfileLastName);
        etEmail = view.findViewById(R.id.etProfileEmail);
        etPassword = view.findViewById(R.id.etProfilePassword);
        etConfirmPassword = view.findViewById(R.id.etProfileConfirmPassword);
        actvGender = view.findViewById(R.id.actvProfileGender);
        ivProfilePic = view.findViewById(R.id.ivAdminProfilePic);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnLogout = view.findViewById(R.id.btnLogout);
        progressBar = view.findViewById(R.id.progressBar);
        etLastName.setEnabled(true);
        etLastName.setFocusableInTouchMode(true);
        String[] genders = {PersonGender.MALE.name(), PersonGender.FEMALE.name()};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(adapter);
        if ((AdminActivity) getActivity() != null) {
            profileController =((AdminActivity) getActivity()).getProfileController();
        }
        imageUploadController = new ImageUploadController(this);
        ivProfilePic.setOnClickListener(v -> showPhotoOptionsDialog());
        getActivity().findViewById(R.id.tvChangePhoto).setOnClickListener(v->showPhotoOptionsDialog());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentAdmin == null || profileController == null) return;

                String firstName = etFirstName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                String genderStr = actvGender.getText().toString();

                try {
                    currentAdmin.setFirstName(firstName);
                    currentAdmin.setLastName(lastName);
                    currentAdmin.setGender(PersonGender.valueOf(genderStr));
                    currentAdmin.setProfilePicturePath(uploadedProfilePicUrl);
                    Log.d("ImageDebug", "2. Sending to DB. URL attached to currentAdmin is: [" + currentAdmin.getProfilePicturePath() + "]");
                    AdminProfileController.ProfileResponse response = profileController.updateProfile(currentAdmin, password, confirmPassword);

                    if (response.status() == AdminProfileController.ProfileStatus.SUCCESS) {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                    } else {
                        showError(response.message());
                    }

                } catch (IllegalArgumentException e) {
                    showError(e.getMessage());
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileController != null) {
                    profileController.logout();
                    Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
        loadAdminData();
    }

    private void loadAdminData() {
            if (profileController == null) return;
            currentAdmin = profileController.getCurrentAdmin();
            if (currentAdmin != null) {
                etFirstName.setText(currentAdmin.getFirstName());
                etLastName.setText(currentAdmin.getLastName());
                etEmail.setText(currentAdmin.getEmail());
                actvGender.setText(currentAdmin.getGender().name(), false);
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
                Toast.makeText(getContext(), "Camera selected", Toast.LENGTH_SHORT).show();
        }
    });
        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            }
    });
        dialog.show();
    }
    @Override
    public void showProgress() {
        if (!isAdded()) return;
        btnSave.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        if (!isAdded()) return;
        btnSave.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onUploadSuccess(String imageUrl) {
        if (!isAdded()) return;
        hideProgress();
        uploadedProfilePicUrl = imageUrl;
        Glide.with(this)
                .load(imageUrl)
                .into(ivProfilePic);
        Toast.makeText(getContext(), "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showError(String message) {
        if (!isAdded()) return;
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}