package com.example.encs5150_project.model.entity;

public class Admin extends Person{
    private double salary;
    private boolean accountStatus;

    public Admin() {
    }
    public Admin(long id, String firstName, String secondName, String email, String password, String confirmPassword,String gender, double salary, boolean accountStatus) {
        super( firstName, secondName, email, password, confirmPassword,gender);
        this.salary = salary;
        this.accountStatus = accountStatus;
    }
    public Admin(long id, String firstName, String secondName, String email, String password, String gender, double salary, boolean accountStatus) {
        super(id, firstName, secondName, email, password, gender);
        this.salary = salary;
        this.accountStatus = accountStatus;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }
}
