package com.example.encs5150_project.model.entity;

public abstract class Person{
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private PersonGender gender;
    private String profilePicturePath;
    private static final int NAME_MIN_NUMBER_OF_CHARACTERS=3;
    private static final int PASSWORD_MIN_NUMBER_OF_CHARACTERS=6;
    private static final int PASSWORD_MIN_NUMBER_OF_LETTERS=1;
    private static final int PASSWORD_MIN_NUMBER_OF_DIGITS=1;
    public Person() {
    }

    public Person(String firstName, String lastName, String email, String password,PersonGender gender) {
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        this.gender = gender;
    }
    public Person(long id,String firstName, String lastName, String email, String password, PersonGender gender) {
        this.id=id;
        setFirstName(firstName);
        setLastName(lastName);
        setEmail(email);
        setPassword(password);
        this.gender = gender;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if(firstName.length()<NAME_MIN_NUMBER_OF_CHARACTERS)
            throw new IllegalArgumentException("Invalid first name, it should contain at minimum "+NAME_MIN_NUMBER_OF_CHARACTERS+" character"+((NAME_MIN_NUMBER_OF_CHARACTERS==1)?"":"s"));
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if(lastName.length()<NAME_MIN_NUMBER_OF_CHARACTERS)
            throw new IllegalArgumentException("Invalid last name, it should contain at minimum "+NAME_MIN_NUMBER_OF_CHARACTERS+" character"+((NAME_MIN_NUMBER_OF_CHARACTERS==1)?"":"s"));
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(!email.matches("^[\\w.%_+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
            throw new IllegalArgumentException("Invalid email, it doesn't follow correct email format");
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }
    private void validatePassword(String password){
        boolean containsLetter=false;
        boolean containsDigit=false;
        if(password.length()<PASSWORD_MIN_NUMBER_OF_CHARACTERS)
            throw new IllegalArgumentException("Invalid password, it should contain at minimum "+PASSWORD_MIN_NUMBER_OF_CHARACTERS+" character"+((PASSWORD_MIN_NUMBER_OF_CHARACTERS==1)?"":"s"));
        for(int i=0;i<password.length();i++) {
            if(Character.isLetter(password.charAt(i)))
                containsLetter=true;
            else if(Character.isDigit(password.charAt(i)))
                containsDigit=true;
        }
        if(!containsLetter)
            throw new IllegalArgumentException("Invalid password, it should contain at minimum "+PASSWORD_MIN_NUMBER_OF_LETTERS+" letter"+((PASSWORD_MIN_NUMBER_OF_LETTERS==1)?"":"s"));
        if(!containsDigit)
            throw new IllegalArgumentException("Invalid password, it should contain at minimum "+PASSWORD_MIN_NUMBER_OF_DIGITS+" digit"+((PASSWORD_MIN_NUMBER_OF_DIGITS==1)?"":"s"));
    }

    public PersonGender getGender() {
        return gender;
    }

    public void setGender(PersonGender gender) {
        this.gender = gender;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

}
