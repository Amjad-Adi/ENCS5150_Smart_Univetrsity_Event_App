package com.example.encs5150_project.controller;

import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.PersonRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.config.DefaultAdmin;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class AuthenticationController {

    private final SharedPrefManager sharedPrefManager;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordHashingAlgorithm passwordHashingAlgorithm;
    private final PersonRepository personRepository;
    private final SQLiteDatabase db;

    public enum AuthStatus {
        SUCCESS_ADMIN,
        SUCCESS_USER,
        SUCCESS_REGISTRATION,
        ERROR_VALIDATION,
        ERROR_DISABLED,
        ERROR_CREDENTIALS,
        ERROR_SYSTEM
    }

    public record AuthResponse(AuthStatus status, String message) {
    }

    public AuthenticationController(SharedPrefManager sharedPrefManager, PersonRepository personRepository,UserRepository userRepository, AdminRepository adminRepository,SQLiteDatabase db ,PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.sharedPrefManager = sharedPrefManager;
        this.personRepository=personRepository;
        this.userRepository = userRepository;
        this.adminRepository=adminRepository;
        this.passwordHashingAlgorithm = passwordHashingAlgorithm;
        this.db=db;
    }

    public String getRememberedEmail() {
        return sharedPrefManager.readString(PreferencesConstants.KEY_REMEMBERED_EMAIL, PreferencesConstants.DEFAULT_REMEMBERED_EMAIL);
    }

    public boolean getRememberMeStatus() {
        return sharedPrefManager.readBoolean(PreferencesConstants.KEY_REMEMBER_ME, PreferencesConstants.DEFAULT_REMEMBER_ME);
    }
    public AuthResponse handleLogin(String email, String password, boolean rememberMe) {
        if (email == null || email.trim().isEmpty()) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Email is required");
        }
        if (password == null || password.trim().isEmpty()) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Password is required");
        }
        String cleanEmail = email.trim();
        try {
            User.validatePassword(password);
            Admin admin = adminRepository.findByEmail(cleanEmail);
            if(admin!=null){
                if(admin.getAccountStatus()==EntityStatus.ENABLED){
                    return finalizeLogin(cleanEmail, rememberMe, AuthStatus.SUCCESS_ADMIN);
                }
                else {
                    return new AuthResponse(AuthStatus.ERROR_DISABLED, "Your account has been deactivated. Please contact support.");
                }
            }
            if (admin != null) {
                if (passwordHashingAlgorithm.verifyPassword(password, admin.getPassword())) {
                    return finalizeLogin(cleanEmail, rememberMe, AuthStatus.SUCCESS_ADMIN);
                } else {
                    return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
                }
            }
            if (cleanEmail.equals(DefaultAdmin.EMAIL)) {
                if (passwordHashingAlgorithm.verifyPassword(password, DefaultAdmin.HASHED_PASSWORD)) {
                    return finalizeLogin(cleanEmail, rememberMe, AuthStatus.SUCCESS_ADMIN);
                } else {
                    return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
                }
            }
            User user = userRepository.findByEmail(cleanEmail);
            if(user!=null){
                if(user.getAccountStatus()==EntityStatus.ENABLED){
                    return finalizeLogin(cleanEmail, rememberMe, AuthStatus.SUCCESS_USER);
                }
                else {
                    return new AuthResponse(AuthStatus.ERROR_DISABLED, "Your account has been deactivated. Please contact support.");
                }
            }
            if (user != null) {
                if (passwordHashingAlgorithm.verifyPassword(password, user.getPassword())) {
                    return finalizeLogin(cleanEmail, rememberMe, AuthStatus.SUCCESS_USER);
                } else {
                    return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
                }
            }
            return new AuthResponse(AuthStatus.ERROR_CREDENTIALS, "Incorrect email or password");
        } catch (IllegalArgumentException e) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(AuthStatus.ERROR_SYSTEM, "An error occurred during authentication.");
        }
    }
    private AuthResponse finalizeLogin(String email, boolean rememberMe, AuthStatus successStatus) {
        sharedPrefManager.writeBoolean(PreferencesConstants.KEY_REMEMBER_ME, rememberMe);
        sharedPrefManager.writeString(PreferencesConstants.KEY_REMEMBERED_EMAIL, rememberMe ? email : PreferencesConstants.DEFAULT_REMEMBERED_EMAIL);
        sharedPrefManager.writeString(PreferencesConstants.KEY_SESSION_EMAIL, email);

        return new AuthResponse(successStatus, email);
    }
    public AuthResponse handleRegistration(String firstName, String lastName, String email, String phone, String password, String confirmPassword, String genderStr, String majorStr) {
        AuthResponse basicValidation = validateBasicFields(firstName, lastName, email, password, confirmPassword, genderStr);
        if (basicValidation != null) return basicValidation;
        if (phone.trim().isEmpty()) return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Phone number is required");
        if (majorStr.isEmpty() || majorStr.equals("Select Category") || majorStr.equals("Select Major")) {
            return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Please select a valid major");
        }
        try {
            User.validatePassword(password);
            if (personRepository.isEmailExists(db,email)) {
                return new AuthResponse(AuthStatus.ERROR_VALIDATION, "This email is already registered");
            }
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
    private AuthResponse validateBasicFields(String fName, String lName, String email, String pass, String confirm, String gender) {
        if (fName.trim().isEmpty() || lName.trim().isEmpty()) return new AuthResponse(AuthStatus.ERROR_VALIDATION, "First and Last names are required");
        if (email.trim().isEmpty()) return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Email is required");
        if (!pass.equals(confirm)) return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Passwords do not match");
        if (gender.isEmpty() || gender.equals("Select Gender")) return new AuthResponse(AuthStatus.ERROR_VALIDATION, "Please select a gender");
        return null;
    }
}