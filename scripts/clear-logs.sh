#!/bin/bash
# Clear the Android log buffer

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

ADB="$SDK_DIR/platform-tools/adb"

echo "Clearing Android log buffer..."
"$ADB" logcat -c

echo "✓ Logs cleared"
echo ""
echo "Run './scripts/view-logs.sh' to view new logs"
