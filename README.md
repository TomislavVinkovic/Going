# **Going \- Event Discovery App**

**Going** is a modern, native Android application built with Jetpack Compose that helps users discover events happening around them. It features an interactive map, social features, and a clean, dynamic UI that adapts to the user's system theme.

## **🎥 App Demo**

![Going App Demo GIF](https://i.ibb.co/F4rbMMT8/ezgif-71ee8ed05f83f2.gif)

## **✨ Features**

* **Interactive Map:** Discover events visually on a Google Map with custom, theme-aware markers.  
* **User Location:** The map automatically centers on your location for immediate discovery.  
* **Event Search:** A powerful search screen with category filters to find the perfect event.  
* **Event Details:** View detailed information about any event by selecting it from the map.  
* **Social Interest:** Mark yourself as "interested" in an event and see who else is going.  
* **User Profiles:** Manage your profile information and picture.  
* **Modern UI:** Built entirely with Jetpack Compose, featuring Material 3 design and dynamic coloring (Material You) on supported devices.  
* **Authentication:** Secure login and registration using Firebase Authentication (Email/Password and Google Sign-In).

## **🛠 Tech Stack & Architecture**

* **Language:** [Kotlin](https://kotlinlang.org/)  
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)  
* **Architecture:** MVVM (Model-View-ViewModel)  
* **Navigation:** [Jetpack Navigation for Compose](https://developer.android.com/jetpack/compose/navigation)  
* **Asynchronous Programming:** Kotlin Coroutines & Flow  
* **Backend:** [Firebase](https://firebase.google.com/)  
  * **Authentication:** Firebase Authentication  
  * **Database:** Cloud Firestore (Real-time)  
* **Maps:** [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview) & Maps Compose Library

## **🚀 Setup and Installation**

To run this project on your own machine, you will need to set up your own Firebase project and obtain a Google Maps API key.

### **Prerequisites**

* Android Studio (latest version recommended)  
* A Google account for Firebase and Google Cloud Console.

### **Step 1: Clone the Repository**

git clone \[https://github.com/your-username/going-app.git\](https://github.com/your-username/going-app.git)  
cd going-app

### **Step 2: Create and Configure Your Firebase Project**

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.  
2. Add an Android app to your project.  
   * **Package Name:** Make sure the package name matches the applicationId in the app/build.gradle.kts file (e.g., com.example.going).  
   * Follow the instructions to download the google-services.json file.  
3. Place the downloaded **google-services.json** file into the **app/** directory of your Android Studio project.  
4. **Enable Authentication:**  
   * In the Firebase Console, go to **Build \> Authentication**.  
   * Click the "Sign-in method" tab.  
   * Enable the **Email/Password** provider.  
   * Enable the **Google** provider. Provide a project support email when prompted.  
5. **Set Up Cloud Firestore:**  
   * Go to **Build \> Cloud Firestore**.  
   * Click "Create database" and start in **Production mode**.  
   * Choose a location for your database (e.g., europe-west).  
   * Go to the **Rules** tab and paste the following rules to allow users to read events and manage their own data:
   ```
     rules\_version \= '2';  
     service cloud.firestore {  
       match /databases/{database}/documents {  
         match /users/{userId} {  
           allow list, create: if true;  
           allow read, update, delete: if request.auth \!= null && request.auth.uid \== userId;  
         }  
         match /events/{eventId} {  
           allow read: if request.auth \!= null;  
         }  
         match /eventInterests/{interestId} {  
             allow read, create, delete: if request.auth \!= null;  
         }  
       }  
     }
   ```

   * You will need to manually create an **events** collection in the Firestore Data tab and add a few sample documents for the app to display something.

### **Step 3: Get Your Google Maps API Key**

1. Go to the [Google Cloud Console](https://console.cloud.google.com/). Make sure you have the same project selected as your Firebase project.  
2. In the navigation menu, go to **APIs & Services \> Library**.  
3. Search for and enable the **Maps SDK for Android**.  
4. Go to **APIs & Services \> Credentials**.  
5. Click **\+ CREATE CREDENTIALS \> API key**.  
6. Copy the generated API key.  
7. Open the app/src/main/AndroidManifest.xml file in Android Studio and replace the placeholder with your key:
   ```
   <meta-data  
       android:name="com.google.android.geo.API\_KEY"  
       android:value="YOUR\_API\_KEY\_HERE" /\>
   ```

### **Step 4: Build and Run the App**

You should now be able to build and run the project in Android Studio.

## **🔮 Future Improvements**

This project is under active development. Here are some of the features planned for the future:

* **Friends List & Activity Feed:** Fully implement the social features, including adding/removing friends, viewing their profiles, and seeing which events they are interested in.  
* **Implement Repository Pattern:** Refactor the data layer to use the Repository pattern to abstract data sources, which will make testing easier and allow for future offline caching capabilities.  
* **Custom Backend:** Migrate from a pure Firebase backend to a custom Laravel backend  for more complex business logic and API control that is not easily achievable with Firestore.  
* **Advanced Search & Filtering:** Enhance the search functionality with more powerful filters, including:  
  * Filtering events by a specific date range.  
  * Searching for events within a certain radius (e.g., 5km, 10km) of the user.  
  * Filtering by event tags.

## **🤝 Contributing**

Contributions are welcome\! If you have suggestions or want to improve the app, please feel free to open an issue or submit a pull request.

## **📄 License**

This project is licensed under the MIT License \- see the [LICENSE.md](http://docs.google.com/LICENSE.md) file for details.
