# 🍲 Meal Explorer 

A feature-rich native Android application designed to help users discover new recipes, explore global cuisines, and save their favorite meals. 

Developed during the Mobile Application Development track at the **Information Technology Institute (ITI)**, this project was built from the ground up prioritizing clean architecture principles, real authentication flows, and a scalable design over a simple demo approach.

## ✨ Core Features

* **🌍 Global Discovery:** Browse an extensive database of meals filtered by specific categories and countries of origin.
* **🔐 Secure Authentication:** Seamless user registration and login flows using Firebase Authentication (Email/Password).
* **⭐ User-Isolated Favorites:** A robust local database implementation that ensures saved favorite recipes are kept strictly isolated per authenticated user.
* **🔍 Smart Search:** Intuitive search functionality to quickly find specific meals or ingredients.
* **📖 Deep-Dive Meal Details:** Comprehensive detail screens displaying ingredients, step-by-step instructions, and embedded YouTube video tutorials for the recipes.

## 🛠 Technical Stack

This application demonstrates a practical implementation of modern Android architectural patterns and libraries:

* **Language:** Java
* **Architecture:** MVP (Model-View-Presenter) for clean separation of concerns
* **Networking:** Retrofit for REST API consumption
* **Reactive Programming:** RxJava for asynchronous data streams and event handling
* **Local Persistence:** Room Database for robust, offline data caching
* **Backend Services:** Firebase Authentication
* **UI & Design:** Material Design components for structured navigation and thoughtful UI/UX

## 🧠 Architecture Highlights

* **Separation of Concerns:** Strict adherence to the MVP pattern ensures that business logic is completely decoupled from UI components, making the codebase highly testable and maintainable.
* **Data Layer Management:** Efficiently handles data from both remote APIs (Retrofit) and local SQLite storage (Room), presenting a unified data stream to the Presenter layer via RxJava.

## 📱 Screenshots

*(Add your screenshots here! Replace the placeholder links with actual paths to your images)*

## 🚀 Try it Out

Want to test the app on your own device? You can download the compiled APK directly here:
📥 **[Download APK via Google Drive](https://lnkd.in/dKqjX5QY)**
