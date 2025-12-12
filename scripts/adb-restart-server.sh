#!/bin/bash
# Check for connected Android devices and emulators

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

ADB="$SDK_DIR/platform-tools/adb"

echo "Restarting ADB server..."
echo ""
"$ADB" kill-server
"$ADB" start-server
