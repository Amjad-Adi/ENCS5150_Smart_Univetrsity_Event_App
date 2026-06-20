package com.example.encs5150_project.controller;

import android.util.Log;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class AdminProfileController {

    private final AdminRepository adminRepository;
    private final SharedPrefManager sharedPrefManager;
    private final PasswordHashingAlgorithm passwordHashingAlgorithm;

    public enum ProfileStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }

    public record ProfileResponse(ProfileStatus status, String message) {}

    public AdminProfileController(AdminRepository adminRepository, SharedPrefManager sharedPrefManager, PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.adminRepository = adminRepository;
        this.sharedPrefManager = sharedPrefManager;
        this.passwordHashingAlgorithm = passwordHashingAlgorithm;
    }

    public Admin getCurrentAdmin() {
            return adminRepository.findByEmail(sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, null));
    }
    public ProfileResponse updateProfile(Admin admin, String newPassword, String confirmPassword) {
        try {
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    return new ProfileResponse(ProfileStatus.ERROR_VALIDATION, "Passwords do not match");
                }
                User.validatePassword(newPassword);
                String hashedPassword = passwordHashingAlgorithm.hashPassword(newPassword);
                admin.setPassword(hashedPassword);
            }
            adminRepository.update(admin);
            return new ProfileResponse(ProfileStatus.SUCCESS, "Profile updated successfully");

        } catch (IllegalArgumentException e) {
            return new ProfileResponse(ProfileStatus.ERROR_VALIDATION, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ProfileResponse(ProfileStatus.ERROR_SYSTEM, "An error occurred while updating the profile.");
        }
    }
    public void logout() {
        sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, null);
    }
}