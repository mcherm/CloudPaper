#!/bin/bash
# View CloudPaper logs from adb logcat

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

ADB="$SDK_DIR/platform-tools/adb"

echo "Viewing CloudPaper logs (Ctrl+C to exit)..."
echo "=========================================="
echo ""

# View logs filtered for CloudPaper
# -v time shows timestamps
# grep filters for just our tag
"$ADB" logcat -v time | grep --line-buffered "CloudPaper"
