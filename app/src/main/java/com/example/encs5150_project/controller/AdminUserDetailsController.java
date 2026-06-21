package com.example.encs5150_project.controller;

import android.database.sqlite.SQLiteDatabase;

import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.PersonRepository;
import com.example.encs5150_project.model.repository.UserRepository;

public class AdminUserDetailsController {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public enum DetailStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }

    public record DetailResponse(DetailStatus status, String message) {}

    public AdminUserDetailsController(UserRepository userRepository,PersonRepository personRepositor) {
        this.userRepository = userRepository;
        this.personRepository=personRepositor;
    }
    public DetailResponse updateUser(User user, String firstName, String lastName, String email, String phone, String genderStr, String majorStr, String profilePicturePath) {
        try {
            if (firstName == null || firstName.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "First name is required.");
            if (lastName == null || lastName.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Last name is required.");
            if (email == null || email.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Email is required.");
            if (phone == null || phone.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Phone number is required.");
            String cleanEmail = email.trim();
            if (!user.getEmail().equalsIgnoreCase(cleanEmail) && personRepository.isEmailExists(cleanEmail)) {
                return new DetailResponse(DetailStatus.ERROR_VALIDATION, "This email is already registered to another account.");
            }
            user.setFirstName(firstName.trim());
            user.setLastName(lastName.trim());
            user.setEmail(cleanEmail);
            user.setPhoneNumber(phone.trim());
            user.setGender(PersonGender.valueOf(genderStr));
            user.setMajor(UserMajor.valueOf(majorStr));
            if (profilePicturePath != null && !profilePicturePath.trim().isEmpty()) {
                user.setProfilePicturePath(profilePicturePath);
            }
            userRepository.update(user);
            return new DetailResponse(DetailStatus.SUCCESS, "User information updated successfully.");
        } catch (IllegalArgumentException e) {
            return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Invalid gender or major selection.");
        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while updating the user.");
        }
    }

    public DetailResponse toggleUserStatus(User user) {
        try {
            if (user.getAccountStatus() == EntityStatus.ENABLED) {
                user.setAccountStatus(EntityStatus.DISABLED);
            } else {
                user.setAccountStatus(EntityStatus.ENABLED);
            }
            userRepository.changeStatus(user.getId(),user.getAccountStatus());
            String statusMsg = user.getAccountStatus() == EntityStatus.ENABLED ? "Account enabled successfully." : "Account disabled successfully.";
            return new DetailResponse(DetailStatus.SUCCESS, statusMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while changing account status.");
        }
    }
}