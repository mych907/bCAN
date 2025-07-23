# AGENT.md

## 📦 Agent Overview

This agent sets up and builds **bCAN**, a lightweight Android application written in Kotlin. bCAN connects to a PC tool via REST API to display real-time CAN data from Kvaser devices on smartphones. It is designed for internal use at bitsensing but structured for future public release.

---

## 🎯 Goals

* Set up the development environment for Android (SDK, build tools, Kotlin, Gradle).
* Automate project dependency installation.
* Build and package the bCAN APK for Android 8.0+ devices.
* Provide simple commands for clean builds and incremental development.

---

## 🏗️ Responsibilities

The agent:

✅ Installs **Android SDK** and command-line tools.
✅ Ensures all required SDK components (`platform-tools`, `build-tools`, `platforms;android-35`) are installed.
✅ Configures `ANDROID_SDK_ROOT` for Kotlin/Gradle builds.
✅ Resolves project dependencies using Gradle.
✅ Builds the APK using Gradle tasks.
✅ Provides commands to run unit tests and lint checks.
✅ Optionally signs the APK for release builds.

---

## 📐 System Requirements

| Component            | Version                            |
| -------------------- | ---------------------------------- |
| OS                   | Linux (Ubuntu 20.04+) or macOS 12+ |
| Java Development Kit | OpenJDK 17                         |
| Android SDK          | Command-line tools (latest)        |
| Kotlin               | 2.0.0                              |
| Gradle               | Wrapper provided                   |
| Node.js (optional)   | ≥ 16 (for Codex tooling)           |

---

## 📁 Project Structure

```
bcan-android/
├── app/
│   ├── src/main/java/com/bitsensing/bcan/
│   │   ├── ui/
│   │   ├── network/
│   │   ├── model/
│   │   ├── repository/
│   │   └── MainActivity.kt
│   └── res/
│       ├── layout/
│       └── values/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
└── gradlew
```

---

## 🚀 Agent Commands

### 🏁 Setup Environment

Installs Android SDK and required tools:

```bash
codex run setup
```

What it does:

* Downloads and unpacks Android command-line tools.
* Sets `ANDROID_SDK_ROOT`.
* Installs SDK components: `platform-tools`, `build-tools`, `platforms;android-35`.

---

### 🛠️ Build Debug APK

Builds the debug APK for testing:

```bash
codex run build-debug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

---

### 📦 Build Release APK

Builds a signed release APK (requires `keystore.properties`):

```bash
codex run build-release
```

Output: `app/build/outputs/apk/release/app-release.apk`

---

### 🧪 Run Unit Tests

Runs all unit and instrumentation tests:

```bash
codex run test
```

---

### 🧹 Clean Build

Cleans all build artifacts:

```bash
codex run clean
```

---

## ⚙️ Configuration

### Environment Variables

| Variable           | Description                    |
| ------------------ | ------------------------------ |
| ANDROID\_SDK\_ROOT | Path to Android SDK            |
| JAVA\_HOME         | Path to OpenJDK 17             |
| GRADLE\_OPTS       | (Optional) JVM args for Gradle |

---

## 📦 Dependencies

| Dependency                        | Purpose          |
| --------------------------------- | ---------------- |
| `com.squareup.retrofit2:retrofit` | REST API client  |
| `com.squareup.okhttp3:okhttp`     | Networking       |
| `com.squareup.moshi:moshi`        | JSON parsing     |
| `androidx.lifecycle:lifecycle`    | State management |
| `androidx.compose.ui:ui`          | UI framework     |

---

## 📝 Notes

* PC Tool API Endpoint must be configured in **Settings > API Endpoint** on first launch.
* Default polling interval: 500ms (can be changed in app settings).
* Future WebSocket support will require adding `okhttp-ws`.
