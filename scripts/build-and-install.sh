#!/bin/bash
# Build and install CloudPaper in one step

# Set JAVA_HOME to Java 21 (required for Gradle 8.13)
source "$(dirname "$0")/set-java-env.sh"

echo ""
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
