package com.example.encs5150_project.controller;

import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.Person;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.preferences.PreferencesConstants;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminManagementController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SharedPrefManager sharedPrefManager;
    private AccountType currentType = AccountType.USER;
    private List<Person> ascendingList = new ArrayList<>();
    private List<Person> descendingList = new ArrayList<>();

    public enum AccountType { USER, ADMIN }

    public AdminManagementController(UserRepository userRepository, AdminRepository adminRepository, SharedPrefManager sharedPrefManager) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.sharedPrefManager = sharedPrefManager;
    }

    public boolean canManageAdmins() {
        String email = sharedPrefManager.readString(PreferencesConstants.KEY_SESSION_EMAIL, null);
        if (email == null) return false;
        Admin currentAdmin = adminRepository.findByEmail(email);
        return currentAdmin != null && currentAdmin.getRole() == AdminRole.ADMINISTRATOR;
    }
    public List<Person> performSearch(String searchBy, boolean isAscending, String query) {
        String cleanQuery = (query == null) ? "" : query;

        if (currentType == AccountType.USER) {
            ascendingList = (List<Person>) (List<?>) userRepository.search(searchBy, true, cleanQuery);
            descendingList = (List<Person>) (List<?>) userRepository.search(searchBy, false, cleanQuery);
        } else {
            if (!canManageAdmins()) {
                ascendingList = Collections.emptyList();
                descendingList = Collections.emptyList();
            } else {
                ascendingList = (List<Person>) (List<?>) adminRepository.search(searchBy, true, cleanQuery);
                descendingList = (List<Person>) (List<?>) adminRepository.search(searchBy, false, cleanQuery);
            }
        }
        return isAscending ? ascendingList : descendingList;
    }
    public List<Person> toggleSortDirection(boolean isAscending) {
        return isAscending ? ascendingList : descendingList;
    }

    public void setAccountType(AccountType type) {
        this.currentType = type;
    }

    public AccountType getCurrentType() {
        return currentType;
    }
}