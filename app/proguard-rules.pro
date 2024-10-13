# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Obfuscation
-repackageclasses
-allowaccessmodification

# Kotlin
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void check*(...);
	public static void throw*(...);
}
-assumenosideeffects class java.util.Objects {
    public static ** requireNonNull(...);
}

# Root Service
-keep class it.dhd.oxygencustomizer.aiplugin.services.* { *; }
-keep class it.dhd.oxygencustomizer.aiplugin.IRootProviderService { *; }

# org.pytorch.PyTorchAndroid
-keepnames class org.pytorch.**
-keep class org.pytorch.** { *; }
-keep class com.facebook.jni.* { *; }

-printusage release/usage.txt
-printmapping release/mapping.txt