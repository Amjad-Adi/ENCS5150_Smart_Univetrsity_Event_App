package com.example.encs5150_project.model.repository;

import android.content.Context;

import com.example.encs5150_project.model.repository.database.DataBaseHelper;

public final class RepositoryManager {

    private final EventRepository eventRepository;
    private final PersonRepository personRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final ReviewRepository reviewRepository;
    private final FavouriteRepository favouriteRepository;

    public RepositoryManager(Context context) {
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(context.getApplicationContext());
        eventRepository = new EventRepository(dataBaseHelper);
        personRepository = new PersonRepository();
        reservationRepository = new ReservationRepository(dataBaseHelper);
        userRepository = new UserRepository(dataBaseHelper);
        adminRepository = new AdminRepository(dataBaseHelper);
        reviewRepository = new ReviewRepository(dataBaseHelper);
        favouriteRepository = new FavouriteRepository(dataBaseHelper);
    }

    public EventRepository getEventRepository() {
        return eventRepository;
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public AdminRepository getAdminRepository() {
        return adminRepository;
    }

    public ReviewRepository getReviewRepository() {
        return reviewRepository;
    }

    public FavouriteRepository getFavouriteRepository() {
        return favouriteRepository;
    }
}
