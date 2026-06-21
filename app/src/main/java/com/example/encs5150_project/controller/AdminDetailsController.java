package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.EntityStatus;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.repository.AdminRepository;

public class AdminDetailsController {

    private final AdminRepository adminRepository;

    public enum DetailStatus {
        SUCCESS,
        ERROR_VALIDATION,
        ERROR_SYSTEM
    }

    public record DetailResponse(DetailStatus status, String message) {}

    public AdminDetailsController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public DetailResponse updateAdmin(Admin admin, String firstName, String lastName, String email, String salaryStr, String genderStr, String roleStr, String profilePicturePath) {
        try {
            if (firstName == null || firstName.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "First name is required.");
            if (lastName == null || lastName.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Last name is required.");
            if (email == null || email.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Email is required.");
            if (salaryStr == null || salaryStr.trim().isEmpty()) return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Salary is required.");
            String cleanEmail = email.trim();
            if (!admin.getEmail().equalsIgnoreCase(cleanEmail)) {
                Admin existingAdmin = adminRepository.findByEmail(cleanEmail);
                if (existingAdmin != null) {
                    return new DetailResponse(DetailStatus.ERROR_VALIDATION, "This email is already registered to another account.");
                }
            }
            double salary;
            try {
                salary = Double.parseDouble(salaryStr.trim());
                if (salary < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Invalid salary amount.");
            }
            admin.setFirstName(firstName.trim());
            admin.setLastName(lastName.trim());
            admin.setEmail(cleanEmail);
            admin.setSalary(salary);
            admin.setGender(PersonGender.valueOf(genderStr));
            admin.setRole(AdminRole.valueOf(roleStr));
            if (profilePicturePath != null && !profilePicturePath.trim().isEmpty()) {
                admin.setProfilePicturePath(profilePicturePath);
            }
            adminRepository.update(admin);
            return new DetailResponse(DetailStatus.SUCCESS, "Admin information updated successfully.");

        } catch (IllegalArgumentException e) {
            return new DetailResponse(DetailStatus.ERROR_VALIDATION, "Invalid gender or role selection.");
        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while updating the admin.");
        }
    }

    public DetailResponse toggleAdminStatus(Admin admin) {
        try {
            if (admin.getAccountStatus() == EntityStatus.ENABLED) {
                admin.setAccountStatus(EntityStatus.DISABLED);
            } else {
                admin.setAccountStatus(EntityStatus.ENABLED);
            }
            adminRepository.changeStatus(admin.getId(), admin.getAccountStatus());
            String statusMsg = admin.getAccountStatus() == EntityStatus.ENABLED ? "Account enabled successfully." : "Account disabled successfully.";
            return new DetailResponse(DetailStatus.SUCCESS, statusMsg);
        } catch (Exception e) {
            e.printStackTrace();
            return new DetailResponse(DetailStatus.ERROR_SYSTEM, "An error occurred while changing account status.");
        }
    }
}