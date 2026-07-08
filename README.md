# ENCS5150_Smart_Univetrsity_Event_App
# Smart University Events Application

A comprehensive Android application developed to simplify the management of university events, reservations, and student engagement through a centralized platform. The application provides separate interfaces for students and administrators while following a clean software architecture based on the Model–View–Controller (MVC) design pattern.
A Java Android application for managing university events, reservations, and student engagement. The app provides separate admin and student workflows: admins manage events, users, and reservations; students discover events, reserve seats, favorite and review events, and manage their profiles.

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
## Table of Contents

---

# Project Overview

The Smart University Events Application enables students to discover university events, reserve seats, manage their participation, submit reviews, maintain a list of favorite events, and manage their personal profiles.

Administrators are provided with a dedicated dashboard that allows them to manage the entire system, including users, events, reservations, reviews, administrators, and application content.

The application was designed with maintainability, scalability, and separation of concerns in mind by applying software engineering principles and design patterns throughout the project.
- Project overview
- Features
- Architecture & stack
- Project structure
- How to build & run
- Configuration (Cloudinary & preferences)
- Data model & storage
- Where to find diagrams and documentation
- Contributing
- Contributors
- License

---

# Features
## Project overview

## Authentication

* User registration
* User login
* Administrator login
* Secure password validation
* Remember Me functionality using SharedPreferences
* Default administrator initialization
* Session persistence
This project is an Android application developed as a course project (ENCS5150) that centralizes university event information and simplifies event discovery, attendance management, and administrative oversight. It targets Android devices and is implemented in Java using the Android SDK and Gradle.

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
## Features (complete)

         Controller Layer
   (Business Logic & Validation)
Authentication & account
- User registration (sign up)
- User login
- Administrator login
- Remember Me / session persistence via SharedPreferences
- Default administrator seeding on first run
- Secure password validation and input checks

                 │
Student (user) features
- Browse university events
- Search and explore events (keyword/filters where provided in UI)
- View detailed event information (date, time, location, description, image)
- Reserve seats for events and cancel reservations
- View reservation history and current reservations
- Add and remove events from favorites (bookmarks)
- Submit reviews for events
- View recommended events (recommendation flow implemented in code)
- Edit personal profile and upload profile image
- Pick images from device and upload to cloud storage (Cloudinary integration)
- Contact university administration (contact/feedback flows where present)

                 ▼
Administrator features
- Administrator dashboard and home overview
- Add new events (title, description, seats, image, date/time, location, etc.)
- Edit and remove events
- View and manage event details and attendees
- Manage users (view user list and user details)
- Manage reservations (view, approve/decline/cancel depending on flow)
- Moderate reviews
- Manage administrator accounts and profiles

            Model Layer
Media & cloud
- Image picking (from device storage or camera) and upload
- Cloudinary integration for persistent image hosting

     Repositories │ Database │ API
     Preferences │ Cloudinary
     Entity Classes
```
Persistence & storage
- Local SQLite database (tables and contracts for entities)
- Repository pattern for all CRUD operations
- SharedPreferences for lightweight settings and session

The architecture separates presentation, business logic, and data access into independent layers, making the project easier to maintain and extend.
Other
- Onboarding / introduction screens
- Background/asynchronous operations for network and uploads
- MVC-like organization separating views, controllers, and models

---

# MVC Implementation

## View

The View layer consists of Android Activities and Fragments.
## Architecture & stack

Responsibilities include:
- Language: Java (Android)
- Runtime / build: Android SDK with Gradle (Gradle wrapper included)
- Pattern: MVC-like (View: Activities/Fragments; Controller: controllers package; Model: entity & repository classes)
- Storage: SQLite (local) + Cloudinary for images
- UI components: RecyclerView, CardView, Material Design components
- Build files: `build.gradle` (top-level) and `app/build.gradle` (module)

* Displaying application data
* Handling user interactions
* Collecting user input
* Updating UI components
* Delegating operations to controllers

The View does **not** communicate directly with the database or repositories.
Notable modules / packages (source-level):
- `com.example.encs5150_project.controller` — controllers for authentication, admin and user flows, image handling
- `com.example.encs5150_project.model` — entity classes, repositories, database helper, API/cloud wrappers
- `com.example.encs5150_project.view` — activities/fragments and UI logic
- `app/src/main/res` — layouts, drawables, strings and resources

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
## Project structure (top-level)

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
/ (repo root)
├─ README.md
├─ ENCS5150-Android-Course-Project.pdf   # Project report/spec
├─ ERD.pdf                              # Database ERD (entities & relationships)
├─ UML.pdf                              # UML diagrams
├─ Smart University App System.png      # System diagram
├─ CONTRIBUTIONS.md, Contributions.pdf  # Contribution notes
├─ app/                                 # Android module
│  ├─ build.gradle
│  └─ src/main/
│     ├─ AndroidManifest.xml
│     ├─ java/com/example/encs5150_project/
│     │   ├─ controller/   # Controllers (AuthenticationController.java, Admin*, User*)
│     │   ├─ model/        # Entities, repositories, database helpers
│     │   └─ view/         # Activities and fragments
│     └─ res/              # layouts, drawables, strings
└─ gradle/ gradlew/ settings.gradle
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
How it fits together:
- Views (Activities / Fragments) collect user input and show data.
- Controllers handle business logic, validate input, call repositories, and update views.
- Repositories manage all SQLite interactions. The DatabaseHelper is a singleton that initializes and upgrades the database and seeds default data (including a default admin).
- Image uploads are handled by controllers that call Cloudinary helper classes in the model layer and then persist returned image URLs.

---

# Database Layer

SQLite is used as the application's local database.

The database layer contains:
## How to build & run

* DataBaseHelper
* Database contracts
* Table creation scripts
* Foreign key relationships
* Default administrator seeding
* Database initialization
Prerequisites
- Java JDK (11+ recommended)
- Android Studio (recommended) with Android SDK and emulator, or an Android device
- Optional: configure `ANDROID_HOME` / `ANDROID_SDK_ROOT` for CLI builds

Each table is represented by a dedicated contract class to centralize schema definitions.

---

# Repository Layer
Quick steps (CLI)
```bash
git clone https://github.com/Amjad-Adi/ENCS5150_Smart_Univetrsity_Event_App.git
cd ENCS5150_Smart_Univetrsity_Event_App

Repositories provide an abstraction between the controller and the SQLite database.
# Build the debug APK using the Gradle wrapper
./gradlew assembleDebug    # macOS / Linux
# or
gradlew.bat assembleDebug  # Windows

Repositories included in the project are:
# Install on a connected device or emulator
./gradlew installDebug
```

* PersonRepository
* UserRepository
* AdminRepository
* EventRepository
* ReservationRepository
* FavouriteRepository
* ReviewRepository
Open in Android Studio
- File > Open > choose the cloned repository root
- Let Android Studio sync Gradle and download dependencies
- Run the `app` module on an emulator or device

Each repository is responsible for Create, Read, Update, and Delete (CRUD) operations related to its corresponding entity.
Notes
- The module `app/` contains the Android application. Use Android Studio Run configurations to launch.
- Inspect `app/src/main/AndroidManifest.xml` for activities and required permissions.

---

# Cloud Integration

The application integrates with **Cloudinary** to manage image uploads.
## Configuration

Cloudinary is used for storing profile images and event images outside the local database, reducing application storage requirements while providing secure cloud-based media management.
Cloudinary (image uploads)
- The app integrates with Cloudinary for image storage. You must configure Cloudinary credentials to enable image uploads.
- Typical configuration (set these in code or via a properties file / secure storage):
  - CLOUDINARY_CLOUD_NAME
  - CLOUDINARY_API_KEY
  - CLOUDINARY_API_SECRET

The ImageUploadController coordinates upload requests and returns image URLs for persistent storage.
SharedPreferences
- The app uses SharedPreferences for "Remember Me" and session persistence. No manual setup required; data is stored in app private storage.

---

# SharedPreferences

SharedPreferences are used to persist lightweight application data, including:
## Data model & storage

* Remember Me functionality
* Logged-in user session
* User preferences
* Application settings

This allows users to remain authenticated across application restarts without repeatedly entering their credentials.
- SQLite is used to persist application data locally. The database schema and relationships are documented in `ERD.pdf`.
- Entity classes include: Person (base), User, Admin, Event, Reservation, Review, Favourite, and enumerations (AdminRole, ReservationStatus, PersonGender, UserMajor, etc.).
- Repositories implement CRUD for each entity (UserRepository, EventRepository, ReservationRepository, FavouriteRepository, ReviewRepository, AdminRepository, PersonRepository).

---

# Application Workflow
## Where to find diagrams and documentation

## Student Workflow
- `ENCS5150-Android-Course-Project.pdf` — full project report and specification
- `ERD.pdf` — database schema and relationships
- `UML.pdf` — UML class and sequence diagrams
- `Smart University App System.png` — system and architecture diagram
- `CONTRIBUTIONS.md` / `Contributions.pdf` — contributions information

1. Launch application.
2. Authenticate or register.
3. Browse available events.
4. View event details.
5. Reserve events.
6. Add favorites.
7. Submit reviews.
8. Manage profile.
Screenshots and visual assets are in the repository root and `app/src/main/res/drawable` where applicable.

---

## Administrator Workflow
## Developer notes

1. Authenticate as administrator.
2. Open administrator dashboard.
3. Manage users.
4. Manage events.
5. Review reservations.
6. Moderate reviews.
7. Manage administrator accounts.
8. Update administrator profile.
- Follow the repository package layout when adding or modifying code.
- Use repository classes for database access; controllers should not perform raw SQL.
- DatabaseHelper is a singleton — avoid creating multiple DB instances.
- For Cloudinary, handle uploads asynchronously and persist the returned image URL in the corresponding entity.
- If adding features that require server-side APIs, document endpoints and expected responses in `app/src/main` or a new `docs/` folder.

---

# Security Considerations

The application incorporates several security measures including:

* Input validation
* Password verification
* Session persistence
* Foreign key enforcement
* Controlled repository access
* Encapsulation of database operations
Testing & debugging
- Use Android Studio's Logcat and the emulator to test flows.
- Inspect `app/src/main/java/.../controller` to follow user and admin workflows.

---

# Installation

1. Clone the repository.
## Contributing

```bash
git clone <repository-url>
```

2. Open the project in Android Studio.

3. Sync Gradle dependencies.

4. Configure Cloudinary credentials if required.
If you want to contribute or extend the project:
1. Fork the repository
2. Create a topic branch: `git checkout -b feat/your-feature`
3. Commit changes with descriptive messages
4. Open a pull request describing the feature or fix

5. Build and run the application on an Android device or emulator.
Please include unit tests or manual test steps for non-trivial changes.

---

## Recommended Environment
## Contributors

To ensure the intended user experience and interface consistency, it is recommended to run the application with the following device settings:

- **System Language:** English
- **Appearance Mode:** Light Mode

The application's layouts, icons, and visual components have been designed and tested primarily under these settings. Other configurations, such as Dark Mode or non-English system languages, are not officially supported and may affect the appearance of certain UI elements.

---
# Contributors
- Amjad Adi
- AbdAlrahman Atyani

* Amjad Adi
* AbdAlrahman Atyani
See `CONTRIBUTIONS.md` for details on individual contributions.

---

# License
## License

This project was developed as part of the **ENCS5150 Mobile Application Development** course at Birzeit University and is intended for educational purposes.
This project was developed for academic purposes (ENCS5150 Mobile Application Development course at Birzeit University). Reuse and redistribution are permitted for educational purposes; please credit the original authors.
