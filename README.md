# ENCS5150_Smart_Univetrsity_Event_App
# Smart University Events Application

A comprehensive Android application developed to simplify the management of university events, reservations, and student engagement through a centralized platform. The application provides separate interfaces for students and administrators while following a clean software architecture based on the Model–View–Controller (MVC) design pattern.

---

# Table of Contents

* Project Overview
* Features
* Technologies
* Software Architecture
* Design Principles & Patterns
* Project Structure
* Application Workflow
* Database Layer
* Repository Layer
* Controller Layer
* Model Layer
* Cloud Integration
* Shared Preferences
* Installation
* Contributors

---

# Project Overview

The Smart University Events Application enables students to discover university events, reserve seats, manage their participation, submit reviews, maintain a list of favorite events, and manage their personal profiles.

Administrators are provided with a dedicated dashboard that allows them to manage the entire system, including users, events, reservations, reviews, administrators, and application content.

The application was designed with maintainability, scalability, and separation of concerns in mind by applying software engineering principles and design patterns throughout the project.

---

# Features

## Authentication

* User registration
* User login
* Administrator login
* Secure password validation
* Remember Me functionality using SharedPreferences
* Default administrator initialization
* Session persistence

---

## Student Features

* Browse university events
* Search and explore events
* View detailed event information
* Reserve seats for events
* Cancel reservations
* View reservation history
* Add events to favorites
* Remove favorites
* Submit event reviews
* Edit personal profile
* Upload profile images
* Contact university administration

---

## Administrator Features

* Administrator dashboard
* Event management
* User management
* Reservation management
* Review management
* Administrator management
* Profile management
* Home dashboard navigation

---

# Technologies Used

* Java
* Android SDK
* SQLite Database
* SharedPreferences
* Cloudinary Image Storage
* Material Design Components
* Android Fragments
* RecyclerView
* CardView
* REST/API Integration
* Background Threads
* Git

---

# Software Architecture

The project follows the **Model–View–Controller (MVC)** architectural pattern.

```
               User

                 │

                 ▼

             View Layer
      (Activities / Fragments)

                 │

                 ▼

         Controller Layer
   (Business Logic & Validation)

                 │

                 ▼

            Model Layer

     Repositories │ Database │ API
     Preferences │ Cloudinary
     Entity Classes
```

The architecture separates presentation, business logic, and data access into independent layers, making the project easier to maintain and extend.

---

# MVC Implementation

## View

The View layer consists of Android Activities and Fragments.

Responsibilities include:

* Displaying application data
* Handling user interactions
* Collecting user input
* Updating UI components
* Delegating operations to controllers

The View does **not** communicate directly with the database or repositories.

---

## Controller

Controllers act as the bridge between the View and the Model.

Responsibilities include:

* Business logic
* Input validation
* Request processing
* Calling repositories
* Calling APIs
* Updating the View with processed data
* Coordinating application flow

Controllers isolate application logic from Android UI code.

---

## Model

The Model layer manages all application data.

It includes:

* Entity classes
* SQLite database
* Database helper
* Repository classes
* SharedPreferences
* Cloudinary integration
* API communication
* Low-level SQL operations

Controllers never perform SQL operations directly. Instead, repositories encapsulate all database interactions.

---

# Design Principles

The application applies several software engineering principles.

## Separation of Concerns

Each layer has a distinct responsibility.

* View → User Interface
* Controller → Business Logic
* Model → Data Management

This reduces complexity and improves maintainability.

---

## Single Responsibility Principle

Each class is designed around a single responsibility.

Examples include:

* AuthenticationController handles authentication.
* EventRepository handles event persistence.
* ImageUploadController manages image uploads.
* DataBaseHelper manages SQLite initialization.

---

## Loose Coupling

Application components interact through controllers and repositories rather than depending directly on implementation details.

This allows each component to evolve independently.

---

## Dependency Injection

Repositories and helper classes are supplied to controllers rather than instantiated throughout the application.

This improves modularity and code organization.

---

## Observer Pattern

Background operations such as image uploads and API requests execute asynchronously.

When an operation completes, callback mechanisms notify the controller, which then updates the View without blocking the UI thread.

---

## Repository Pattern

Every major entity has a dedicated repository responsible for data persistence.

Repositories abstract SQLite operations and expose reusable methods to the controller layer.

Examples include:

* UserRepository
* AdminRepository
* EventRepository
* ReservationRepository
* FavouriteRepository
* ReviewRepository

---

## Singleton Pattern

The SQLite helper is implemented as a Singleton to ensure that the application maintains a single database instance throughout its lifecycle.

---

# Project Structure

```
com.example.encs5150_project

│
├── controller
├── model
│   ├── entity
│   ├── repository
│   ├── database
│   ├── contracts
│   ├── api
│   ├── preferences
│   └── cloud
│
├── view
│
├── adapter
│
├── util
│
└── resources
```

---

# Controller Layer

The controller package contains the application's business logic.

### Authentication

* AuthenticationController
* IntroductionController

Responsible for authentication, registration, session management, and application startup.

---

### Profile Management

* ProfileManagementController
* ImageUploadController

Responsible for user profile updates and image uploading.

---

### Student Controllers

* UserHomeController
* UserEventController
* UserFavoriteController
* UserReservationController
* UserReviewController
* UserContactUsController
* UserSpecialSectionFragment

Responsible for all student functionality.

---

### Administrator Controllers

* AdminDashboardController
* AdminHomeController
* AdminManagementController
* AdminProfileController
* AdminUserManagementController
* AdminEventManagementController
* AdminReservationManagementController
* AdminReviewManagementController

Responsible for administrative operations.

---

# Model Layer

## Entity Classes

The project defines entity classes representing database objects.

### Person

Base class containing common user information.

### User

Represents student accounts.

### Admin

Represents administrator accounts.

### Event

Represents university events.

### Reservation

Represents event reservations.

### Review

Represents user reviews.

### Favourite

Represents bookmarked events.

### Enumerations

The project also includes several enumerations to ensure data consistency.

* AdminRole
* EntityStatus
* ReservationStatus
* ReservationType
* PersonGender
* UserMajor

---

# Database Layer

SQLite is used as the application's local database.

The database layer contains:

* DataBaseHelper
* Database contracts
* Table creation scripts
* Foreign key relationships
* Default administrator seeding
* Database initialization

Each table is represented by a dedicated contract class to centralize schema definitions.

---

# Repository Layer

Repositories provide an abstraction between the controller and the SQLite database.

Repositories included in the project are:

* PersonRepository
* UserRepository
* AdminRepository
* EventRepository
* ReservationRepository
* FavouriteRepository
* ReviewRepository

Each repository is responsible for Create, Read, Update, and Delete (CRUD) operations related to its corresponding entity.

---

# Cloud Integration

The application integrates with **Cloudinary** to manage image uploads.

Cloudinary is used for storing profile images and event images outside the local database, reducing application storage requirements while providing secure cloud-based media management.

The ImageUploadController coordinates upload requests and returns image URLs for persistent storage.

---

# SharedPreferences

SharedPreferences are used to persist lightweight application data, including:

* Remember Me functionality
* Logged-in user session
* User preferences
* Application settings

This allows users to remain authenticated across application restarts without repeatedly entering their credentials.

---

# Application Workflow

## Student Workflow

1. Launch application.
2. Authenticate or register.
3. Browse available events.
4. View event details.
5. Reserve events.
6. Add favorites.
7. Submit reviews.
8. Manage profile.

---

## Administrator Workflow

1. Authenticate as administrator.
2. Open administrator dashboard.
3. Manage users.
4. Manage events.
5. Review reservations.
6. Moderate reviews.
7. Manage administrator accounts.
8. Update administrator profile.

---

# Security Considerations

The application incorporates several security measures including:

* Input validation
* Password verification
* Session persistence
* Foreign key enforcement
* Controlled repository access
* Encapsulation of database operations

---

# Installation

1. Clone the repository.

```bash
git clone <repository-url>
```

2. Open the project in Android Studio.

3. Sync Gradle dependencies.

4. Configure Cloudinary credentials if required.

5. Build and run the application on an Android device or emulator.

---

# Contributors

* Amjad Adi
* AbdAlrahman Atyani

---

# License

This project was developed as part of the **ENCS5150 Mobile Application Development** course at Birzeit University and is intended for educational purposes.
