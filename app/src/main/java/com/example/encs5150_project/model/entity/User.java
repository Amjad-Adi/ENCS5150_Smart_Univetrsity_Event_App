package com.example.encs5150_project.model.entity;

public class User extends Person{
    private UserMajor major;
    private String phoneNumber;
    private EntityStatus accountStatus;
    public User(String email, String password) {
        setEmail(email);
        setPassword(password);
    }
    public User(String firstName, String secondName, String email, String password, PersonGender gender, UserMajor major, String phoneNumber, EntityStatus accountStatus) {
        super(firstName, secondName, email, password, gender);
        this.major = major;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }
    public User(long id, String firstName, String secondName, String email, String password, PersonGender gender, UserMajor major, String phoneNumber, EntityStatus accountStatus) {
        super(id, firstName, secondName, email, password, gender);
        this.major = major;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }
    public UserMajor getMajor() {
        return major;
    }

    public void setMajor(UserMajor major) {
        this.major = major;
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
