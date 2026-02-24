#!/bin/sh
# Gradle wrapper stub — downloads and runs Gradle
# This is a minimal version; GitHub Actions' setup-gradle handles the real setup

APP_NAME="Gradle"
GRADLE_VERSION="8.5"
GRADLE_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"

GRADLE_USER_HOME="${GRADLE_USER_HOME:-$HOME/.gradle}"
GRADLE_WRAPPER_DIR="$GRADLE_USER_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin"

if [ ! -d "$GRADLE_WRAPPER_DIR" ]; then
    mkdir -p "$GRADLE_WRAPPER_DIR"
    echo "Downloading Gradle $GRADLE_VERSION..."
    curl -sL "$GRADLE_URL" -o "$GRADLE_WRAPPER_DIR/gradle.zip"
    unzip -q "$GRADLE_WRAPPER_DIR/gradle.zip" -d "$GRADLE_WRAPPER_DIR"
fi

GRADLE_HOME=$(find "$GRADLE_WRAPPER_DIR" -maxdepth 1 -name "gradle-*" -type d | head -1)
exec "$GRADLE_HOME/bin/gradle" "$@"
