# Android ProGuard rules
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# -proguard-files option in build.gradle.
# For more details, see
#   https://developer.android.com/studio/build/shrink-code

# Use this to preserve the line number information for
# debugging stack traces and exception messages
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name and line number from the user.
#-renamesourcefileattribute SourceFile

# Keep the AndroidManifest.xml file
-keep class android.support.** { *; }
-keep class androidx.** { *; }
-keep class com.google.android.material.** { *; }
