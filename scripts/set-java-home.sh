#!/bin/bash
# Set JAVA_HOME to Java 17 for building CloudPaper
# This script should be sourced by other scripts that need to run Gradle

# Check if Java 17 is installed via Homebrew
if [ -d "/usr/local/opt/openjdk@17" ]; then
    export JAVA_HOME="/usr/local/opt/openjdk@17"
elif [ -d "/opt/homebrew/opt/openjdk@17" ]; then
    # For Apple Silicon Macs
    export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
else
    echo "Error: Java 17 not found at expected Homebrew locations"
    echo "Please install Java 17 with: brew install openjdk@17"
    return 1 2>/dev/null || exit 1
fi

# Verify Java 17 is available
if [ ! -x "$JAVA_HOME/bin/java" ]; then
    echo "Error: Java 17 binary not found at $JAVA_HOME/bin/java"
    return 1 2>/dev/null || exit 1
fi
