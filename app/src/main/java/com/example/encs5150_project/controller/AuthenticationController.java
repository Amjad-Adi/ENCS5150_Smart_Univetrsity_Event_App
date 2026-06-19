package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.config.AdminCredentials;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class AuthenticationController {

    private final SharedPrefManager sharedPrefManager;
    private final UserRepository userRepository;
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

    public AuthenticationController(SharedPrefManager sharedPrefManager, UserRepository userRepository, PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.sharedPrefManager = sharedPrefManager;
        this.userRepository = userRepository;
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
            new User(email, password);
        } catch (IllegalArgumentException e) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, e.getMessage());
        }
        sharedPrefManager.writeBoolean(PreferencesConstants.KEY_REMEMBER_ME, rememberMe);
        sharedPrefManager.writeString(PreferencesConstants.KEY_REMEMBERED_EMAIL, rememberMe ? email : PreferencesConstants.DEFAULT_REMEMBERED_EMAIL);
        try {
            if (email.equals(AdminCredentials.EMAIL) && password.equals(AdminCredentials.PASSWORD)) {
                sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, email);
                return new AuthResponse(AuthStatus.SUCCESS_ADMIN, email);
            }
            User user = userRepository.findByEmail(email);
            if (user != null && passwordHashingAlgorithm.verifyPassword(password, user.getPassword())) {
                sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, user.getEmail());
                return new AuthResponse(AuthStatus.SUCCESS_USER, user.getEmail());
            } else {
                return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
            }
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
            PersonGender gender = PersonGender.valueOf(genderStr);
            UserMajor major = UserMajor.valueOf(majorStr);
            User newUser = new User(firstName, lastName, email, password, gender, major, phone, EntityStatus.ENABLED);
            String hashedPassword = passwordHashingAlgorithm.hashPassword(password);
            newUser.setPassword(hashedPassword);
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