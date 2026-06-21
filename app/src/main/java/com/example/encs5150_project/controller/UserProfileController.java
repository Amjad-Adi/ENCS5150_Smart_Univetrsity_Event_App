package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class UserProfileController {

    private final UserRepository userRepository;
    private final SharedPrefManager sharedPrefManager;
    private final PasswordHashingAlgorithm passwordHashingAlgorithm;

    public enum ProfileStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }

    public record ProfileResponse(ProfileStatus status, String message) {}

    public UserProfileController(UserRepository userRepository, SharedPrefManager sharedPrefManager, PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.userRepository = userRepository;
        this.sharedPrefManager = sharedPrefManager;
        this.passwordHashingAlgorithm = passwordHashingAlgorithm;
    }

    public User getCurrentUser() {
        return userRepository.findByEmail(sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, null));
    }

    public ProfileResponse updateProfile(User user, String newPassword, String confirmPassword) {
        try {
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    return new ProfileResponse(ProfileStatus.ERROR_VALIDATION, "Passwords do not match");
                }
                User.validatePassword(newPassword);
                String hashedPassword = passwordHashingAlgorithm.hashPassword(newPassword);
                user.setPassword(hashedPassword);
            }
            userRepository.update(user);
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