# CloudPaper Development Scripts

This folder contains helper scripts to simplify common development tasks.

## Available Scripts

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
