package com.example.encs5150_project.model.entity;

public class User extends Person{
    private UserMajor major;
    private String profilePicturePath;
    private String phoneNumber;
    private EntityStatus accountStatus;
    public User(String firstName, String secondName, String email, String password, String confirmPassword, PersonGender gender, UserMajor major, String profilePicturePath, String phoneNumber, EntityStatus accountStatus) {
        super(firstName, secondName, email, password,confirmPassword, gender);
        this.major = major;
        this.profilePicturePath = profilePicturePath;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }
    public User(long id, String firstName, String secondName, String email, String password, PersonGender gender, UserMajor major, String profilePicturePath, String phoneNumber, EntityStatus accountStatus) {
        super(id, firstName, secondName, email, password, gender);
        this.major = major;
        this.profilePicturePath = profilePicturePath;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }
    public UserMajor getMajor() {
        return major;
    }

    public void setMajor(UserMajor major) {
        this.major = major;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public EntityStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(EntityStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
