# ENCS5150 Smart University Events App

A Java Android application for managing university events, reservations, and student engagement. The app provides separate admin and student workflows: admins manage events, users, and reservations; students discover events, reserve seats, favorite and review events, and manage their profiles.

---

## Table of Contents

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

## Project overview

This project is an Android application developed as a course project (ENCS5150) that centralizes university event information and simplifies event discovery, attendance management, and administrative oversight. It targets Android devices and is implemented in Java using the Android SDK and Gradle.

---

## Features (complete)

Authentication & account
- User registration (sign up)
- User login
- Administrator login
- Remember Me / session persistence via SharedPreferences
- Default administrator seeding on first run
- Secure password validation and input checks

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

Administrator features
- Administrator dashboard and home overview
- Add new events (title, description, seats, image, date/time, location, etc.)
- Edit and remove events
- View and manage event details and attendees
- Manage users (view user list and user details)
- Manage reservations (view, approve/decline/cancel depending on flow)
- Moderate reviews
- Manage administrator accounts and profiles

Media & cloud
- Image picking (from device storage or camera) and upload
- Cloudinary integration for persistent image hosting

Persistence & storage
- Local SQLite database (tables and contracts for entities)
- Repository pattern for all CRUD operations
- SharedPreferences for lightweight settings and session

Other
- Onboarding / introduction screens
- Background/asynchronous operations for network and uploads
- MVC-like organization separating views, controllers, and models

---

## Architecture & stack

- Language: Java (Android)
- Runtime / build: Android SDK with Gradle (Gradle wrapper included)
- Pattern: MVC-like (View: Activities/Fragments; Controller: controllers package; Model: entity & repository classes)
- Storage: SQLite (local) + Cloudinary for images
- UI components: RecyclerView, CardView, Material Design components
- Build files: `build.gradle` (top-level) and `app/build.gradle` (module)

Notable modules / packages (source-level):
- `com.example.encs5150_project.controller` — controllers for authentication, admin and user flows, image handling
- `com.example.encs5150_project.model` — entity classes, repositories, database helper, API/cloud wrappers
- `com.example.encs5150_project.view` — activities/fragments and UI logic
- `app/src/main/res` — layouts, drawables, strings and resources

---

## Project structure (top-level)

```
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

How it fits together:
- Views (Activities / Fragments) collect user input and show data.
- Controllers handle business logic, validate input, call repositories, and update views.
- Repositories manage all SQLite interactions. The DatabaseHelper is a singleton that initializes and upgrades the database and seeds default data (including a default admin).
- Image uploads are handled by controllers that call Cloudinary helper classes in the model layer and then persist returned image URLs.

---

## How to build & run

Prerequisites
- Java JDK (11+ recommended)
- Android Studio (recommended) with Android SDK and emulator, or an Android device
- Optional: configure `ANDROID_HOME` / `ANDROID_SDK_ROOT` for CLI builds

Quick steps (CLI)
```bash
git clone https://github.com/Amjad-Adi/ENCS5150_Smart_Univetrsity_Event_App.git
cd ENCS5150_Smart_Univetrsity_Event_App

# Build the debug APK using the Gradle wrapper
./gradlew assembleDebug    # macOS / Linux
# or
gradlew.bat assembleDebug  # Windows

# Install on a connected device or emulator
./gradlew installDebug
```

Open in Android Studio
- File > Open > choose the cloned repository root
- Let Android Studio sync Gradle and download dependencies
- Run the `app` module on an emulator or device

Notes
- The module `app/` contains the Android application. Use Android Studio Run configurations to launch.
- Inspect `app/src/main/AndroidManifest.xml` for activities and required permissions.

---

## Configuration

Cloudinary (image uploads)
- The app integrates with Cloudinary for image storage. You must configure Cloudinary credentials to enable image uploads.
- Typical configuration (set these in code or via a properties file / secure storage):
  - CLOUDINARY_CLOUD_NAME
  - CLOUDINARY_API_KEY
  - CLOUDINARY_API_SECRET

SharedPreferences
- The app uses SharedPreferences for "Remember Me" and session persistence. No manual setup required; data is stored in app private storage.

---

## Data model & storage

- SQLite is used to persist application data locally. The database schema and relationships are documented in `ERD.pdf`.
- Entity classes include: Person (base), User, Admin, Event, Reservation, Review, Favourite, and enumerations (AdminRole, ReservationStatus, PersonGender, UserMajor, etc.).
- Repositories implement CRUD for each entity (UserRepository, EventRepository, ReservationRepository, FavouriteRepository, ReviewRepository, AdminRepository, PersonRepository).

---

## Where to find diagrams and documentation

- `ENCS5150-Android-Course-Project.pdf` — full project report and specification
- `ERD.pdf` — database schema and relationships
- `UML.pdf` — UML class and sequence diagrams
- `Smart University App System.png` — system and architecture diagram
- `CONTRIBUTIONS.md` / `Contributions.pdf` — contributions information

Screenshots and visual assets are in the repository root and `app/src/main/res/drawable` where applicable.

---

## Developer notes

- Follow the repository package layout when adding or modifying code.
- Use repository classes for database access; controllers should not perform raw SQL.
- DatabaseHelper is a singleton — avoid creating multiple DB instances.
- For Cloudinary, handle uploads asynchronously and persist the returned image URL in the corresponding entity.
- If adding features that require server-side APIs, document endpoints and expected responses in `app/src/main` or a new `docs/` folder.

Testing & debugging
- Use Android Studio's Logcat and the emulator to test flows.
- Inspect `app/src/main/java/.../controller` to follow user and admin workflows.

---

## Contributing

If you want to contribute or extend the project:
1. Fork the repository
2. Create a topic branch: `git checkout -b feat/your-feature`
3. Commit changes with descriptive messages
4. Open a pull request describing the feature or fix

Please include unit tests or manual test steps for non-trivial changes.

---

## Contributors

- Amjad Adi
- AbdAlrahman Atyani

See `CONTRIBUTIONS.md` for details on individual contributions.

---

## License

This project was developed for academic purposes (ENCS5150 Mobile Application Development course at Birzeit University). Reuse and redistribution are permitted for educational purposes; please credit the original authors.
