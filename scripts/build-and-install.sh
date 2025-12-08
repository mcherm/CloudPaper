#!/bin/bash
# Build and install CloudPaper in one step

echo "================================"
echo "CloudPaper: Build and Install"
echo "================================"
echo ""

# Build
echo "Step 1: Building APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo ""
    echo "✗ Build failed"
    exit 1
fi

echo ""
echo "Step 2: Installing APK..."
./scripts/install-apk.sh
