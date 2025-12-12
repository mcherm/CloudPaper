#!/bin/bash
# Build the CloudPaper release AAB for Google Play Store

# Set JAVA_HOME to Java 17
source "$(dirname "$0")/set-java-home.sh"

echo "Building CloudPaper release bundle..."
echo ""

# Check if keystore exists
KEYSTORE_PATH="$HOME/.android/cloudpaper-release.keystore"
if [ ! -f "$KEYSTORE_PATH" ]; then
    echo "✗ Error: Keystore not found at $KEYSTORE_PATH"
    echo ""
    echo "Please create the keystore first using:"
    echo "  keytool -genkey -v -keystore ~/.android/cloudpaper-release.keystore -alias cloudpaper -keyalg RSA -keysize 2048 -validity 10000"
    exit 1
fi

# Check if signing configuration exists in build.gradle
if ! grep -q "signingConfigs" app/build.gradle; then
    echo "✗ Error: Signing configuration not found in app/build.gradle"
    echo ""
    echo "Please add signing configuration to app/build.gradle first."
    echo "See the build.gradle.signing-example file for reference."
    exit 1
fi

echo "Keystore: $KEYSTORE_PATH"
echo ""

# Check if password is set via environment variable
if [ -n "$KEYSTORE_PASSWORD" ]; then
    echo "Using password from environment variable"
    echo ""
    ./gradlew bundleRelease -PKEYSTORE_PASSWORD="$KEYSTORE_PASSWORD" -PKEY_PASSWORD="$KEYSTORE_PASSWORD"
else
    # Prompt for password
    echo "Enter keystore password:"
    read -s PASSWORD
    echo ""
    echo "Building..."
    echo ""
    ./gradlew bundleRelease -PKEYSTORE_PASSWORD="$PASSWORD" -PKEY_PASSWORD="$PASSWORD"
fi

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Build successful!"
    echo ""
    echo "Release bundle (AAB) location:"
    echo "  app/build/outputs/bundle/release/app-release.aab"
    echo ""
    echo "This AAB file is ready for upload to Google Play Console."
    echo ""
    echo "Next steps:"
    echo "  1. Go to https://play.google.com/console"
    echo "  2. Create a new app or select your app"
    echo "  3. Upload the AAB file in the Release section"
else
    echo ""
    echo "✗ Build failed"
    echo ""
    echo "Common issues:"
    echo "  - Incorrect keystore or key password"
    echo "  - Missing signing configuration in build.gradle"
    echo "  - Check the error messages above for details"
    exit 1
fi
