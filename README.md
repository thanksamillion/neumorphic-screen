# Neumorphic Screen

A modern neumorphic design live wallpaper for Android.

## About

This project implements a neumorphic design live wallpaper for Android, featuring soft UI elements with subtle shadows and highlights that create a modern, minimalist aesthetic. The wallpaper displays a beautiful gradient effect with soft shadows and light highlights characteristic of neumorphic design.

## Features

- Neumorphic design with soft shadows and highlights
- Lightweight and battery-efficient
- Minimal permissions required
- Supports Android 5.0 (API 21) and above

## Building the APK

### Manual Build

To build the APK locally:

```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/release/`

### GitHub Actions

This project includes a GitHub Actions workflow that can be manually triggered to build the APK:

1. Go to the **Actions** tab in the GitHub repository
2. Select **Build APK** workflow
3. Click **Run workflow**
4. Choose build type (release or debug)
5. Download the APK from the workflow artifacts

## Installation

1. Download the APK from the releases or build it yourself
2. Install the APK on your Android device
3. Long-press on your home screen
4. Select "Wallpapers" or "Styles & wallpapers"
5. Choose "Live Wallpapers"
6. Select "Neumorphic Live Wallpaper"
7. Tap "Set wallpaper"

## Development

### Requirements

- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API 34

### Project Structure

- `app/src/main/java/` - Java source files
- `app/src/main/res/` - Android resources
- `app/src/main/AndroidManifest.xml` - App manifest

## License

To be determined.
