# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Users\ThinkPad\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-dontpreverify
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes Annotation
-keepattributes Signature
-keep class android.**{*;}
# -------------系统类不需要混淆 --------------------------
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.**
-keep public class com.android.vending.licensing.ILicensingService
#  不混淆个推推送sdk
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
#  不混淆bugly sdk
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
#  不混淆jedis sdk
-dontwarn redis.clients.**
-keep class redis.clients.**{*;}
#  不混淆baidu sdk
-keep class com.baidu.** {*;}
-dontwarn com.baidu.**

-dontwarn jcifs.**
-keep class jcifs.** {*;}

-dontwarn jcifs.**
-keep class jcifs.** {*;}

-dontwarn jsqlite.**
-keep class jsqlite.** {*;}

-dontwarn com.esri.**
-keep class com.esri.** { *;}


-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}

-dontwarn de.greenrobot.dao.**
-keep class de.greenrobot.dao.** { *;}

-dontwarn org.codehaus.jackson.map.ext.**
-keep class org.codehaus.jackson.** {*;}

-dontwarn org.apache.commons.lang3.**
-keep class org.apache.commons.lang3.** { *;}

-dontwarn org.kobjects.**
-keep class org.kobjects.** { *;}

-dontwarn org.ksoap2.**
-keep class org.ksoap2.** { *;}

-dontwarn org.kxml2.**
-keep class org.kxml2.** { *;}

-keep class com.otitan.entity.** { *; } #实体类不参与混淆
-keep class com.otitan.customui.** { *; } #自定义控件不参与混淆


-keepclasseswithmembernames class * {
    #class_specification 不混淆类及其成员
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#}
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
