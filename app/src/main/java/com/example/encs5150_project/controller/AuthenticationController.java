package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.config.DefaultAdmin;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class AuthenticationController {

    private final SharedPrefManager sharedPrefManager;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordHashingAlgorithm passwordHashingAlgorithm;

    public enum AuthStatus {
        SUCCESS_ADMIN,
        SUCCESS_USER,
        SUCCESS_REGISTRATION,
        ERROR_VALIDATION,
        ERROR_CREDENTIALS,
        ERROR_SYSTEM
    }

    public record AuthResponse(AuthStatus status, String message) {
    }

    public AuthenticationController(SharedPrefManager sharedPrefManager, UserRepository userRepository, AdminRepository adminRepository, PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.sharedPrefManager = sharedPrefManager;
        this.userRepository = userRepository;
        this.adminRepository=adminRepository;
        this.passwordHashingAlgorithm = passwordHashingAlgorithm;
    }

    public String getRememberedEmail() {
        return sharedPrefManager.readString(PreferencesConstants.KEY_REMEMBERED_EMAIL, PreferencesConstants.DEFAULT_REMEMBERED_EMAIL);
    }

    public boolean getRememberMeStatus() {
        return sharedPrefManager.readBoolean(PreferencesConstants.KEY_REMEMBER_ME, PreferencesConstants.DEFAULT_REMEMBER_ME);
    }
    public AuthResponse handleLogin(String email, String password, boolean rememberMe) {
        try {
            User.validatePassword(password);
        } catch (IllegalArgumentException e) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, e.getMessage());
        }

        sharedPrefManager.writeBoolean(PreferencesConstants.KEY_REMEMBER_ME, rememberMe);
        sharedPrefManager.writeString(PreferencesConstants.KEY_REMEMBERED_EMAIL, rememberMe ? email : PreferencesConstants.DEFAULT_REMEMBERED_EMAIL);

        try {
            Admin admin = adminRepository.findByEmail(email);
            if (admin != null) {
                if (passwordHashingAlgorithm.verifyPassword(password, admin.getPassword())) {
                    sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, admin.getEmail());
                    return new AuthResponse(AuthStatus.SUCCESS_ADMIN, admin.getEmail());
                } else {
                    return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
                }
            }
            if (email.equals(DefaultAdmin.EMAIL) && passwordHashingAlgorithm.verifyPassword(password, DefaultAdmin.HASHED_PASSWORD)) {
                sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, email);
                return new AuthResponse(AuthStatus.SUCCESS_ADMIN, email);
            }

            User user = userRepository.findByEmail(email);
            if (user != null) {
                if (passwordHashingAlgorithm.verifyPassword(password, user.getPassword())) {
                    sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, user.getEmail());
                    return new AuthResponse(AuthStatus.SUCCESS_USER, user.getEmail());
                } else {
                    return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
                }
            }
            return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(AuthStatus.ERROR_SYSTEM, "An error occurred during authentication.");
        }
    }

    public AuthResponse handleRegistration(String firstName, String lastName, String email, String phone, String password, String confirmPassword, String genderStr, String majorStr) {
        if (!password.equals(confirmPassword))
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Passwords do not match");
        if (phone.isEmpty())
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Phone number is required");
        if (genderStr.equals("Select Gender"))
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Please select a gender");
        if (majorStr.equals("Select Category") || majorStr.equals("Select Major"))
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Please select a valid major");
        if (userRepository.isEmailExists(email))
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "This email is already registered");

        try {
            User.validatePassword(password);
            PersonGender gender = PersonGender.valueOf(genderStr);
            UserMajor major = UserMajor.valueOf(majorStr);
            String hashedPassword = passwordHashingAlgorithm.hashPassword(password);
            User newUser = new User(firstName, lastName, email, hashedPassword, gender, major, phone, EntityStatus.ENABLED);
            userRepository.insert(newUser);
            return new AuthResponse(AuthStatus.SUCCESS_REGISTRATION, "Registration successful");
        } catch (IllegalArgumentException e) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(AuthStatus.ERROR_SYSTEM, "App error occurred.");
        }
    }
}