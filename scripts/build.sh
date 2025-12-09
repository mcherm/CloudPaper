#!/bin/bash
# Build the CloudPaper APK

# Set JAVA_HOME to Java 21 (required for Gradle 8.13)
source "$(dirname "$0")/set-java-env.sh"

echo ""
echo "Building CloudPaper APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo ""
    echo "APK location: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install on device/emulator, run:"
    echo "  ./scripts/install-apk.sh"
else
    echo ""
    echo "✗ Build failed"
    exit 1
fi
