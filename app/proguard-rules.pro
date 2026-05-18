# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Room
-keep class androidx.room.** { *; }
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.Entity { *; }
-keep class androidx.room.Dao { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class kotlin.Metadata { *; }

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }

# Vico Charts
-keep class com.patrykandpatrick.vico.** { *; }