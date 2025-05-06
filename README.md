# OneLove Dating App

A sophisticated Android dating and social discovery app leveraging modern mobile development technologies with AI-enhanced profile discovery and interactive social features.

## Features

- **User Profiles:** Create and manage detailed user profiles with photos, interests, and preferences
- **Match Discovery:** Advanced algorithm for finding compatible matches based on preferences
- **Chat System:** Real-time chat with message notifications and media sharing
- **Offers System:** Send and receive date offers with negotiation features
- **Video/Audio Calls:** Premium in-app calling functionality
- **AI Profiles:** Engage with AI profiles with dynamic response generation
- **Points & Rewards:** Gamification system to encourage app engagement
- **Admin Panel:** Comprehensive admin dashboard for app management
- **Multi-tier Verification:** Identity verification system for user safety
- **Subscription Management:** Stripe integration for premium subscriptions
- **Dynamic Content:** Server-controlled content and feature adjustments

## Technology Stack

- **Frontend:** Jetpack Compose for modern UI
- **Architecture:** MVVM pattern with clean architecture separation
- **Backend:** Firebase (Firestore, Authentication, Storage, Functions)
- **Dependency Injection:** Hilt
- **Messaging:** Firebase Cloud Messaging for notifications
- **Payment Processing:** Stripe API integration
- **Media:** WebRTC for video calls

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/kilagee/onelove/
│   │   │   ├── data/           # Data layer with repositories and sources
│   │   │   ├── di/             # Dependency injection modules
│   │   │   ├── domain/         # Domain layer with models and use cases
│   │   │   ├── service/        # Background services and FCM 
│   │   │   ├── ui/             # UI components and screens
│   │   │   ├── util/           # Utility classes
│   │   │   └── OneLoveApplication.kt
│   │   └── res/                # Resources (layouts, strings, etc.)
│   ├── test/                   # Unit tests
│   └── androidTest/            # Instrumentation tests
├── build.gradle.kts            # App-level Gradle build config
└── proguard-rules.pro          # ProGuard rules
```

## Firebase Setup

The app requires a Firebase project with the following services enabled:

- Authentication (Email/Password, Google, and Phone)
- Firestore Database
- Storage
- Cloud Functions
- Cloud Messaging
- Crashlytics

### Security Rules

The project includes security rules for Firestore (`firebase/firestore.rules`) and Storage (`firebase/storage.rules`) that should be deployed to your Firebase project.

## Development Setup

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 11 or higher
- Android SDK with API level 33+ (Android 13.0+)
- Firebase project with required services
- Stripe API keys (for payment features)

### Configuration

1. Clone the repository
2. Create a Firebase project at [https://console.firebase.google.com/](https://console.firebase.google.com/)
3. Add an Android app to your Firebase project with package name `com.kilagee.onelove`
4. Download the `google-services.json` file and place it in the `app/` directory
5. Enable the required Firebase services (Authentication, Firestore, Storage, Functions, Messaging)
6. Deploy the Firestore and Storage security rules from the `firebase/` directory
7. Deploy the Firebase functions from the `functions/` directory
8. Create a `local.properties` file in the project root with the following content:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key
   STRIPE_SECRET_KEY=your_stripe_secret_key
   ```
9. Build and run the project

## Production Deployment

### Building for Release

1. Update `versionCode` and `versionName` in `app/build.gradle.kts`
2. Generate a signing key if you don't have one:
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
   ```
3. Create a `keystore.properties` file in the project root with your keystore details:
   ```properties
   storeFile=/path/to/release-key.jks
   storePassword=your_keystore_password
   keyAlias=release
   keyPassword=your_key_password
   ```
4. Build the release APK or App Bundle:
   ```bash
   ./gradlew assembleRelease
   # or for App Bundle
   ./gradlew bundleRelease
   ```
5. Test the release build thoroughly before submission

### Google Play Submission

1. Create a developer account on [Google Play Console](https://play.google.com/console/)
2. Create a new application
3. Upload your App Bundle or APK
4. Fill in the store listing details:
   - Title: OneLove Dating App
   - Short description: Discover authentic connections with OneLove, the AI-enhanced dating app
   - Full description: (See marketing materials)
   - Add screenshots and promotional graphics
5. Set up content rating, pricing and distribution
6. Submit for review

## Maintenance

### Firebase Remote Config

The app uses Firebase Remote Config to control feature flags and app settings. The following parameters should be configured:

- `min_version_code`: Minimum app version code that is supported
- `maintenance_mode`: Boolean to enable/disable maintenance mode
- `premium_features`: JSON array of feature IDs that require premium subscription
- `ai_response_rate`: Speed of AI profile responses (1-10)

### Database Backups

Set up regular backups of Firestore data using Firebase's export functionality:

```bash
firebase firestore:export gs://your-backup-bucket/backups/$(date +%Y%m%d)
```

### Monitoring

1. Set up Firebase Crashlytics alerts for crash reporting
2. Configure Firebase Performance Monitoring for app performance insights
3. Use Firebase Analytics to track user behavior and app usage

## License

[Insert your license information here]

## Contact

For questions or support, contact development@kilagee.com