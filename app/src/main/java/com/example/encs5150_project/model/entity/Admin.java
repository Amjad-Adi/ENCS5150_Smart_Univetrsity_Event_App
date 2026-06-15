package com.example.encs5150_project.model.entity;

public class Admin extends Person{
    private double salary;
    AdminRole role;
    private EntityStatus accountStatus;

    public Admin() {
    }
    public Admin(String firstName, String secondName, String email, String password, String confirmPassword,PersonGender gender, double salary,AdminRole role ,EntityStatus accountStatus) {
        super( firstName, secondName, email, password, confirmPassword,gender);
        setSalary(salary);
        this.accountStatus = accountStatus;
        this.role=role;
    }
    public Admin(long id, String firstName, String secondName, String email, String password, PersonGender gender, double salary,AdminRole role , EntityStatus accountStatus) {
        super(id, firstName, secondName, email, password, gender);
        setSalary(salary);
        this.accountStatus = accountStatus;
        this.role=role;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if(salary<0)
            throw new IllegalArgumentException("Salary must be positive");
        this.salary = salary;
    }

    public AdminRole getRole() {
        return role;
    }

    public void setRole(AdminRole role) {
        this.role = role;
    }

    public EntityStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(EntityStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}
