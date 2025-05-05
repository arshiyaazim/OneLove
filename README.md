# OneLove Dating App

A sophisticated Android dating and social discovery app leveraging modern mobile development technologies with AI-enhanced profile discovery and interactive social features.

## Key Technologies
- Jetpack Compose for dynamic UI
- Firebase for real-time backend and authentication
- Cloud Functions for serverless logic
- MVVM architectural pattern
- Firestore for scalable data management
- Notification system with Firebase Cloud Messaging

## Features

### Core Features
- User Authentication (Email/Password)
- Profile Creation and Management
- Match Discovery and Swiping
- Real-time Chat with Matched Users
- Audio/Video Calling (Premium Feature)
- Offers System
- Points & Rewards System
- Subscription Tiers
- Premium Feature Access Control
- Push Notifications

### AI Profile System
- 1000+ preloaded AI profiles with unique personalities
- AI chat with simulated responses based on personality types
- Various response types (flirty, romantic, serious, etc.)
- Premium feature access control

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog | 2023.1.1 or newer
- JDK 17
- Gradle 8.0+
- Firebase Project (with Firestore, Auth, and Storage enabled)

### Local Setup
1. Clone this repository
2. Update `local.properties` with your Android SDK path
3. Create a Firebase project and add the `google-services.json` to the `app/` directory
4. Sync Gradle and build the project

### Firebase Configuration
This project requires the following Firebase services:
- Authentication (Email/Password)
- Firestore Database
- Cloud Storage
- Cloud Messaging
- Cloud Functions

### Running the App
1. Build the app using Android Studio or Gradle
2. Deploy to a physical device or emulator running Android 8.0+

## Project Structure

### Domain Layer
Contains business logic and models:
- `domain/model/` - Data models (User, Match, Message, AIProfile, etc.)
- `domain/repository/` - Repository interfaces
- `domain/usecase/` - Business logic use cases

### Data Layer
Implements the repositories and handles data sources:
- `data/repository/` - Repository implementations
- `data/database/` - Room database for local storage
- `data/model/` - Data transfer objects and mappers

### UI Layer
Uses MVVM pattern with Jetpack Compose:
- `ui/screens/` - Compose UI screens
- `ui/viewmodels/` - ViewModels for each screen
- `ui/components/` - Reusable Compose components
- `ui/theme/` - App theming

### Utilities
- `util/` - Helper classes and utilities
- `util/PremiumAccessManager.kt` - Controls access to premium features

## Premium Features
The following features require a subscription:
- AI Chat
- Video/Audio Calls
- Unlimited Offers
- Profile Boost
- Ad-Free Experience
- Undo Swipe
- Custom Themes

## Current Development Status
- ✅ User Authentication
- ✅ Firebase Integration
- ✅ Home Screen UI
- ✅ Navigation
- ✅ Premium Access Control
- ✅ AI Profile System (Models & Repository)
- ⏳ Chat Implementation
- ⏳ Matching System
- ⏳ Video/Audio Calls
- ⏳ Offers System
- ⏳ Points System
- ⏳ Settings Screens