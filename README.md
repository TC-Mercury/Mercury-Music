# MercuryMusic Player

> A robust, efficient, and responsive local music player for Android devices.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Glide](https://img.shields.io/badge/Glide-Image%20Loading-blue?style=for-the-badge)

## Screenshots

| Song List | Player Interface | Mini Player |
|:---:|:---:|:---:|
| <img src="screenshots/Mercury Music List.png" width="250"> | <img src="screenshots/Mercury Music Player.png" width="250"> | <img src="screenshots/Mercury Music Mini Player.png" width="250"> |

*(Note: Screenshots demonstrate the responsive UI on various screen sizes, including legacy devices like Samsung A8)*

## Features

* **Smart Media Retrieval:** Utilizes Android's native `MediaStore API` to efficiently scan and list audio files from internal storage.
* **Dynamic UI:** Implemented with `ConstraintLayout` to ensure a consistent experience across different aspect ratios and screen sizes (Solved fragmentation issues on physical-button devices).
* **Album Art Integration:** Seamless image loading and caching using the **Glide** library.
* **State Management:**
    * **Singleton Pattern:** Used `MyMediaPlayer` class to manage playback state across activities.
    * **Mini Player:** Persistent bottom player that synchronizes with the main player activity.
* **Smooth Animations:** Custom scale animations for play/pause actions.

## Tech Stack

* **Language:** Java
* **IDE:** Android Studio Narwhal
* **Architecture:** MVC Pattern with Singleton State Manager
* **Key Libraries:**
    * `com.github.bumptech.glide:glide` (Image Processing)
    * `androidx.constraintlayout` (Responsive UI)
    * `androidx.recyclerview` (Efficient Listing)

## Roadmap (Future Improvements)

* [ ] **Foreground Service:** Enable background playback when the app is minimized.
* [ ] **Notification Controls:** Add media controls to the system notification bar.
* [ ] **Favorites:** Implementation of a local database (Room/SQLite) for favorite songs.
* [ ] **Search Functionality:** Filter songs by title or artist.
