# Example ProGuard rules file
# Keep the main class if you're running a Java application

-keep public class com.yourpackage.MainClass {
    public static void main(java.lang.String[]);
}

# Keep all public classes from a specific package
-keep public class com.yourpackage.** { *; }

# Keep Gson classes (or any other libraries you want to keep intact)
-keep class com.google.gson.** { *; }

# Minify and obfuscate everything else
-dontwarn
-dontoptimize
-optimizations !code/simplification/arithmetic
