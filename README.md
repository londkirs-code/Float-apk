# QuickFloat

An Android floating accessibility shortcut menu with a WhatsApp-inspired green interface.

## Build APK
GitHub Actions builds a debug APK on every push. Open **Actions → Android APK Build** and download the `QuickFloat-debug-apk` artifact when the run succeeds.

## Features
- Floating overlay bubble
- Accessibility service actions: Back, Home, Recents, Notifications, Quick Settings, Power dialog (Android/API dependent)
- Hebrew RTL configuration screen
- Uses Android AccessibilityService and overlay permission

## Build locally
Open this project in Android Studio or run `./gradlew assembleDebug` with JDK 17 and Android SDK installed.

Accessibility capabilities vary by Android version and device. The user must explicitly enable the accessibility service in system settings.