package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.entity.UserMajor;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.PersonRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;

public class AddAccountController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordHashingAlgorithm passwordHashingAlgorithm;
    private final PersonRepository personRepository;
    public enum AddStatus { SUCCESS, ERROR_VALIDATION, ERROR_SYSTEM }
    public record AddResponse(AddStatus status, String message) {}

    public AddAccountController(UserRepository userRepository, AdminRepository adminRepository,PersonRepository personRepository, PasswordHashingAlgorithm passwordHashingAlgorithm) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.personRepository=personRepository;
        this.passwordHashingAlgorithm = passwordHashingAlgorithm;
    }
    public AddResponse addUser(String firstName, String lastName, String email, String phone, String password, String confirmPassword, String genderStr, String majorStr) {
        AddResponse basicValidation = validateBasicFields(firstName, lastName, email, password, confirmPassword, genderStr);
        if (basicValidation != null) return basicValidation;
        if (phone == null || phone.trim().isEmpty()) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Phone number is required");
        }
        if (majorStr == null || majorStr.isEmpty() || majorStr.equals("Select Category")) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Please select a valid major");
        }
        try {
            User.validatePassword(password);
            if (personRepository.isEmailExists(email)) {
                return new AddResponse(AddStatus.ERROR_VALIDATION, "This email is already registered");
            }
            String hashedPassword = passwordHashingAlgorithm.hashPassword(password);
            User newUser = new User(firstName, lastName, email, hashedPassword, PersonGender.valueOf(genderStr), UserMajor.valueOf(majorStr), phone, EntityStatus.ENABLED);
            userRepository.insert(newUser);
            return new AddResponse(AddStatus.SUCCESS, "User account created successfully");

        } catch (IllegalArgumentException e) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AddResponse(AddStatus.ERROR_SYSTEM, "System error occurred");
        }
    }

    public AddResponse addAdmin(String firstName, String lastName, String email, String password, String confirmPassword, String genderStr, String salaryStr, String roleStr) {
        AddResponse basicValidation = validateBasicFields(firstName, lastName, email, password, confirmPassword, genderStr);
        if (basicValidation != null) return basicValidation;
        if (salaryStr == null || salaryStr.trim().isEmpty()) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Salary is required");
        }
        if (roleStr == null || roleStr.isEmpty()) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Role is required");
        }
        try {
            double salary = Double.parseDouble(salaryStr);
            if (salary <= 0) return new AddResponse(AddStatus.ERROR_VALIDATION, "Salary must be greater than 0");

            User.validatePassword(password);
            if (personRepository.isEmailExists(email)) {
                return new AddResponse(AddStatus.ERROR_VALIDATION, "This email is already registered");
            }
            String hashedPassword = passwordHashingAlgorithm.hashPassword(password);
            Admin newAdmin = new Admin(firstName, lastName, email, hashedPassword, PersonGender.valueOf(genderStr), salary, AdminRole.valueOf(roleStr), EntityStatus.ENABLED);
            adminRepository.insert(newAdmin);
            return new AddResponse(AddStatus.SUCCESS, "Admin account created successfully");
        } catch (NumberFormatException e) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, "Invalid salary format");
        } catch (IllegalArgumentException e) {
            return new AddResponse(AddStatus.ERROR_VALIDATION, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new AddResponse(AddStatus.ERROR_SYSTEM, "System error occurred");
        }
    }

    private AddResponse validateBasicFields(String fName, String lName, String email, String pass, String confirm, String gender) {
        if (fName.trim().isEmpty() || lName.trim().isEmpty()) return new AddResponse(AddStatus.ERROR_VALIDATION, "First and Last names are required");
        if (email.trim().isEmpty()) return new AddResponse(AddStatus.ERROR_VALIDATION, "Email is required");
        if (!pass.equals(confirm)) return new AddResponse(AddStatus.ERROR_VALIDATION, "Passwords do not match");
        if (gender.isEmpty() || gender.equals("Select Gender")) return new AddResponse(AddStatus.ERROR_VALIDATION, "Please select a gender");
        return null;
    }
}