Student Name: Taha Uğur Toyran
Student Number: 220201055
Course: CENG 443 - Mobile Applications
Final Project: Conference Registration System

Project Description:
This Android application is developed using Kotlin and follows the MVVM (Model-View-ViewModel) architecture strictly. It utilizes the Room Database for local data persistence.

Architecture & Technologies:
- Language: Kotlin
- Architecture: MVVM
- Database: Room Persistence Library
- Concurrency: Kotlin Coroutines & LiveData
- UI Components: XML Layouts, Spinner, RadioGroup, RecyclerView (implied), Intent (Camera & Browser)

Features:
1. Navigation: A custom Spinner based navigation is implemented to switch between "Registration" and "Verification" activities seamlessly.
2. Module A (Registration):
   - Allows users to input ID, Name, Title, and Registration Type.
   - Integrated Camera Intent to capture and save profile photos to internal storage.
   - Saves all data to the local Room database.
   - Includes a button to visit the conference website via Browser Intent.
3. Module B (Verification):
   - Queries the database using User ID.
   - Dynamically changes the background color based on the registration type (Green for Full, Blue for Student, Orange for None).
   - Displays a Red background and error message if the user is not found.

Testing:
The application has been tested on API 35 (Android 15) and handles permissions for the camera dynamically.
