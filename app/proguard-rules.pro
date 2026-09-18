# Default ProGuard rules for Android & CameraX builds

-keepclassmembers class * {
    @androidx.camera.core.** <methods>;
}

# Preserve line numbers for stack traces
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
