# bCAN

bCAN is a lightweight Android application that connects to a PC tool via REST API to display real-time CAN data. The PC tool reads CAN traffic from Kvaser devices, parses the data and serves it over HTTP. This repository contains the Android client.

## Modules
- **app** – Android application written in Kotlin and Jetpack Compose.

## Building
```
./gradlew assembleDebug
```
An installed Android SDK is required. Set `ANDROID_HOME` or create a `local.properties` file pointing `sdk.dir` to your Android SDK path.

## License
MIT
