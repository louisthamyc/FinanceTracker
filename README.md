# FinanceTracker

FinanceTracker is a simple personal Android project. Its main purpose is to experiment with GitHub Actions CI/CD, including running tests, building a signed Android App Bundle, and publishing it to the Google Play internal testing track.

The app is a basic finance tracker for recording and reviewing transactions. It is built with Kotlin and Jetpack Compose, with Room for local data storage.

## CI/CD workflow

The workflow in `.github/workflows/deploy-internal-testing.yml` runs when code is pushed to the `master` branch. It:

1. Checks out the repository and configures JDK 17 and Gradle.
2. Authenticates to Google Cloud using Workload Identity Federation.
3. Runs the `testDebugUnitTest` unit test task.
4. Builds a signed release bundle with `bundleRelease`.
5. Uploads the bundle to the Google Play internal testing track.

To enable the publishing workflow, configure these GitHub Actions repository secrets:

| Secret | Purpose |
| --- | --- |
| `WIP_PROVIDER` | Google Cloud Workload Identity Provider resource name used for GitHub Actions authentication. |
| `SIGNING_KEY_BASE64` | Base64-encoded Android signing keystore. |
| `SIGNSTORE_PASSWORD` | Keystore password. |
| `SIGN_KEY_ALIAS` | Signing key alias. |
| `SIGN_KEY_PASSWORD` | Signing key password. |

The Google Cloud service account configured in the workflow also needs the permissions required to publish the app to Google Play. Keep signing credentials and keystore files private; do not commit them to the repository.

## Build locally

### Requirements

- Android Studio with the Android SDK required by the project (compile SDK 37)
- JDK 17

Open the project in Android Studio and allow Gradle to sync, or build from the repository root:

```bash
./gradlew assembleDebug
```

On Windows, use:

```powershell
\.\gradlew.bat assembleDebug
```

Run the unit tests with:

```bash
./gradlew testDebugUnitTest
```

The release build expects signing values from environment variables or `local.properties`. For local builds, provide `SIGNSTORE_PASSWORD`, `SIGN_KEY_ALIAS`, and `SIGN_KEY_PASSWORD`, and place the corresponding keystore at `app/financeTrackerKey.jks`.

## Tech stack

- Kotlin
- Jetpack Compose and Material 3
- AndroidX Navigation
- Room
- Hilt
- Kotlin Coroutines
- Gradle Kotlin DSL

## Project status

This is a personal learning project and CI/CD test. It is not intended as a production-ready finance application.
