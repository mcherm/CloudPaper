#!/bin/bash
# Install the CloudPaper APK on a connected device/emulator

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

ADB="$SDK_DIR/platform-tools/adb"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

# Check if APK exists
if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK not found at $APK_PATH"
    echo "Run './gradlew assembleDebug' first to build the APK"
    exit 1
fi

# Check for connected devices
DEVICES=$("$ADB" devices | tail -n +2 | grep -v "^$")
if [ -z "$DEVICES" ]; then
    echo "Error: No devices/emulators connected"
    echo "Start an emulator with './scripts/start-emulator.sh' or connect a device"
    exit 1
fi

echo "Installing CloudPaper APK..."
"$ADB" install -r "$APK_PATH"

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Installation successful!"
    echo ""
    echo "To set as wallpaper:"
    echo "  1. Long-press on home screen"
    echo "  2. Select 'Wallpapers'"
    echo "  3. Choose 'Live Wallpapers'"
    echo "  4. Select 'CloudPaper'"
    echo "  5. Tap 'Set wallpaper'"
else
    echo ""
    echo "✗ Installation failed"
    exit 1
fi
