# CloudPaper Development Scripts

This folder contains helper scripts to simplify common development tasks.

## Available Scripts

### `set-java-home.sh`
Sets JAVA_HOME to Java 17 for building CloudPaper. This script is automatically sourced by build scripts.

```bash
source ./scripts/set-java-home.sh
```

**Note:** This script is designed to be sourced (not executed directly) by other build scripts. It automatically detects Java 17 installed via Homebrew.

**Prerequisites:** Java 17 must be installed via Homebrew (`brew install openjdk@17`)

---

### `start-emulator.sh`
Starts the Android emulator (Medium_Phone_API_35).

```bash
./scripts/start-emulator.sh
```

**Note:** This will open the emulator window and keep running. The emulator takes 30-60 seconds to fully boot.

---

### `check-devices.sh`
Checks for connected Android devices and emulators.

```bash
./scripts/check-devices.sh
```

Use this to verify that an emulator is running or a device is connected before installing.

---

### `build.sh`
Builds the CloudPaper debug APK.

```bash
./scripts/build.sh
```

The APK will be created at: `app/build/outputs/apk/debug/app-debug.apk`

---

### `install-apk.sh`
Installs the CloudPaper APK on a connected device or emulator.

```bash
./scripts/install-apk.sh
```

**Prerequisites:**
- An emulator must be running or a device connected
- The APK must be built first (run `build.sh`)

---

### `build-and-install.sh`
Builds the APK and installs it in one step (most commonly used).

```bash
./scripts/build-and-install.sh
```

**Prerequisites:** An emulator must be running or a device connected

---

### `build-release.sh`
Builds the signed release bundle (AAB) for Google Play Store submission.

```bash
./scripts/build-release.sh
```

This creates an Android App Bundle (AAB) file signed with your release keystore. The script will prompt you to enter your keystore password securely (password is not displayed as you type).

**Output:** `app/build/outputs/bundle/release/app-release.aab`

**Prerequisites:**
- Release keystore must exist at `~/.android/cloudpaper-release.keystore`
- You must know the keystore password

**Alternative:** Set environment variable to skip password prompt:
```bash
export KEYSTORE_PASSWORD='your_keystore_password'
./scripts/build-release.sh
```

**Note:** The same password is used for both the keystore and the key entry (as configured when the keystore was created).

---

### `view-logs.sh`
View real-time CloudPaper debug logs from the device/emulator.

```bash
./scripts/view-logs.sh
```

This shows all log messages from CloudPaper. Very useful for debugging! Press Ctrl+C to exit.

**Prerequisites:** A device/emulator must be running

---

### `clear-logs.sh`
Clear the Android log buffer.

```bash
./scripts/clear-logs.sh
```

Use this before testing to get a clean log view without old messages.

---

## Typical Workflow

### First Time Setup
1. Start the emulator:
   ```bash
   ./scripts/start-emulator.sh
   ```
2. Wait for emulator to fully boot (30-60 seconds)
3. In a new terminal window, build and install:
   ```bash
   ./scripts/build-and-install.sh
   ```

### After Making Code Changes
If the emulator is already running:
```bash
./scripts/build-and-install.sh
```

### Setting CloudPaper as Wallpaper
After installation:
1. Long-press on the emulator home screen
2. Select **Wallpapers**
3. Choose **Live Wallpapers**
4. Select **CloudPaper**
5. Tap **Set wallpaper**

---

## Troubleshooting

### "No devices/emulators connected"
- Run `./scripts/check-devices.sh` to verify status
- Start emulator with `./scripts/start-emulator.sh`
- Wait for emulator to fully boot before installing

### "APK not found"
- Run `./scripts/build.sh` first to build the APK

### "Could not find sdk.dir in local.properties"
- Ensure `local.properties` exists in the project root
- Verify it contains the correct Android SDK path

---

## Manual Commands

If you prefer to run commands manually:

**Check devices:**
```bash
/Users/mcherm/Library/Android/sdk/platform-tools/adb devices
```

**Install APK:**
```bash
/Users/mcherm/Library/Android/sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk
```

**Start emulator:**
```bash
/Users/mcherm/Library/Android/sdk/emulator/emulator -avd Medium_Phone_API_35
```
