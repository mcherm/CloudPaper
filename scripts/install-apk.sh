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
DEVICE_COUNT=$("$ADB" devices | tail -n +2 | grep -v "^$" | wc -l | tr -d ' ')

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo "Error: No devices/emulators connected"
    echo ""
    echo "Please connect a device or start an emulator:"
    echo "  - To start emulator: ./scripts/start-emulator.sh"
    echo "  - To connect device: Enable USB debugging and connect via USB"
    exit 1
elif [ "$DEVICE_COUNT" -gt 1 ]; then
    echo "Error: Multiple devices/emulators detected"
    echo ""
    echo "Connected devices:"
    "$ADB" devices | tail -n +2 | grep -v "^$" | while read line; do
        echo "  - $line"
    done
    echo ""
    echo "Please disconnect all but one device/emulator:"
    echo "  - To stop emulator: Close the emulator window"
    echo "  - To disconnect physical device: Unplug USB cable or disable USB debugging"
    echo ""
    echo "Then run './scripts/check-devices.sh' to verify only one device remains"
    exit 1
fi

echo "Device detected:"
"$ADB" devices | tail -n +2 | grep -v "^$"
echo ""

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
