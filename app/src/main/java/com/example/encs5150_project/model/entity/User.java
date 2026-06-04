package com.example.encs5150_project.model.entity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends Person{
    private UserMajor major;
    private URL profilePicturePath;
    private String phoneNumber;
    private boolean accountStatus;
    private final List<Reservation> reservationList=new ArrayList<>();
    private final Set<Favourite> favouriteSet=new HashSet<>();
    public User() {
    }
    public User(String firstName, String secondName, String email, String password, String confirmPassword, String gender, UserMajor major, URL profilePicturePath, String phoneNumber, boolean accountStatus) {
        super(firstName, secondName, email, password,confirmPassword, gender);
        this.major = major;
        this.profilePicturePath = profilePicturePath;
        this.phoneNumber = phoneNumber;
        this.accountStatus = accountStatus;
    }
    public User(long id, String firstName, String secondName, String email, String password, String gender, UserMajor major, URL profilePicturePath, String phoneNumber, boolean accountStatus) {
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

    public URL getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(URL profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(boolean accountStatus) {
        this.accountStatus = accountStatus;
    }
    public boolean addReservation(Reservation reservation) {
        for (Reservation reservationEntity:reservationList)
            if (reservationEntity.getUser()==reservation.getUser() && reservationEntity.getEvent()==reservation.getEvent() &&
                    reservationEntity.getReservationStatus() != ReservationStatus.DELETED_BY_USER)
                return false;
        return reservationList.add(reservation);
    }

    public void removeReservation(Reservation reservation) {
        reservationList.remove(reservation);
    }
    public List<Reservation> getReservationList() {
        return Collections.unmodifiableList(reservationList);
    }
    public boolean addFavourite(Favourite favourite) {
        return favouriteSet.add(favourite);
    }

    public boolean removeFavourite(Favourite favourite) {
        return favouriteSet.remove(favourite);
    }
    public Set<Favourite> getFavouriteSet() {
        return Collections.unmodifiableSet(favouriteSet);
    }
}
