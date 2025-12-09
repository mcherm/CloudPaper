#!/bin/bash
# Set JAVA_HOME to Java 21 for building CloudPaper
# This project requires Java 21 due to Gradle 8.13 compatibility
#
# Usage: source this file or call it directly
#   source scripts/set-java-env.sh
#   OR
#   ./scripts/set-java-env.sh && ./gradlew build

# Set JAVA_HOME to Java 21 (Amazon Corretto 21)
export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-21.jdk/Contents/Home

# Verify Java version
if [ -x "$JAVA_HOME/bin/java" ]; then
    echo "Using Java: $JAVA_HOME"
    "$JAVA_HOME/bin/java" -version 2>&1 | head -1
else
    echo "Warning: Java not found at $JAVA_HOME"
    exit 1
fi
