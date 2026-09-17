# Android Studio / Gradle project for BizEthiopia

This folder contains a real Android app project for BizEthiopia built with Kotlin and Jetpack Compose.

## Structure
- `settings.gradle.kts`
- `build.gradle.kts`
- `app/build.gradle.kts`
- `app/src/main/...`

## Open in Android Studio
1. Open Android Studio
2. Choose "Open"
3. Select this folder: `android/BizEthiopiaApp`
4. Let Gradle sync
5. Build and run on an emulator or connected device

## Features
- Sales dashboard
- Expense tracking
- Inventory overview
- Customer debts
- Orders view
- Mobile-first design
- Material 3 UI

## Installable APK
Once the project is synced and the Android SDK is available, you can build a debug APK from Android Studio or run:

```bash
./gradlew assembleDebug
```

The APK will be generated in `app/build/outputs/apk/debug/`.
