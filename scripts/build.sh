#!/bin/bash
# Build the CloudPaper APK

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
