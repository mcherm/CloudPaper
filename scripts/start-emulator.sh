#!/bin/bash
# Start the Android emulator

# Get the SDK path from local.properties
SDK_DIR=$(grep "sdk.dir" local.properties | cut -d'=' -f2)

if [ -z "$SDK_DIR" ]; then
    echo "Error: Could not find sdk.dir in local.properties"
    exit 1
fi

# Start the emulator
echo "Starting emulator: Medium_Phone_API_35"
"$SDK_DIR/emulator/emulator" -avd Medium_Phone_API_35
