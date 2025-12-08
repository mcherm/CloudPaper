#!/bin/bash
# Check for connected Android devices and emulators

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

ADB="$SDK_DIR/platform-tools/adb"

echo "Checking for connected devices..."
echo ""
"$ADB" devices

echo ""
DEVICES=$("$ADB" devices | tail -n +2 | grep -v "^$")
if [ -z "$DEVICES" ]; then
    echo "No devices or emulators are currently connected."
    echo ""
    echo "To start an emulator, run:"
    echo "  ./scripts/start-emulator.sh"
else
    echo "Device(s) found and ready!"
fi
