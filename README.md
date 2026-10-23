# Smart Pantry Manager

## Project Description

Smart Pantry Manager is an Android application developed in Java using Android Studio. The application helps users manage their pantry ingredients and reduce food waste by suggesting recipes they can prepare using ingredients they already have.

## Features

- Add, view, edit, and delete pantry ingredients.
- Store ingredient names, quantities, units, and optional expiry dates.
- Suggest recipes based on available ingredients and required quantities.
- Display recipes missing exactly one ingredient in a separate Almost There section.
- View recipe ingredients and preparation instructions.
- Input validation to prevent invalid ingredient entries.
- Settings screen for application preferences.

## Database

The application uses Firebase Cloud Firestore to store pantry ingredients and 18 predefined recipes.

Firebase was chosen because it provides cloud-based data storage, supports real-time updates, and allows information to remain available after the application is closed and reopened.

Firebase Anonymous Authentication is used to assign each user a unique ID, allowing their pantry ingredients to be stored separately without requiring registration or login.

## Technologies Used

- Java
- Android Studio
- Firebase Cloud Firestore
- Firebase Authentication
- XML layouts
- RecyclerView

## Setup and Run Instructions

1. Download or clone this GitHub repository.
2. Open the project in Android Studio.
3. Configure a Firebase project with Cloud Firestore and Anonymous Authentication enabled.
4. Add the Firebase configuration file (`google-services.json`) to the `app` folder.
5. Allow Gradle to synchronise the project dependencies.
6. Start an Android emulator or connect an Android device.
7. Run the application using Android Studio.

## Recipe Matching

The application compares the ingredients in the user's pantry with the ingredients required by each recipe.

## Application Demonstration Video

The application demonstration video is available in this repository as Mobile App demo video.mp4.

To watch the demonstration, click the video file, select Download, and open the downloaded MP4 using a video player.


A recipe is only suggested when all required ingredients are available in sufficient quantities. Recipes missing exactly one ingredient are displayed separately under Almost There.

## Developer

Developed as part of the Mobile App Development 700 practical assignment at Richfield Graduate Institute of Technology.
