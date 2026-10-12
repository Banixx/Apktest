# Dockerfile to build the Retro Dialer APK
FROM openjdk:17-jdk-slim

# Install Android SDK
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O android-sdk-tools.zip && \
    unzip android-sdk-tools.zip -d /opt/android-sdk && \
    rm android-sdk-tools.zip && \
    export ANDROID_SDK_ROOT=/opt/android-sdk && \
    yes | /opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk --install "platforms;android-34" "build-tools;34.0.0" "platform-tools" "emulator" && \
    export PATH=$PATH:/opt/android-sdk/platform-tools && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Set Android SDK environment
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:/opt/android-sdk/platform-tools:/opt/android-sdk/cmdline-tools/latest/bin

WORKDIR /app
COPY . .

# Build the APK
RUN cd /app && \
    chmod +x gradlew && \
    ./gradlew assembleDebug

# The APK will be at /app/app/build/outputs/apk/debug/app-debug.apk
