-optimizationpasses 7                           #设置混淆的压缩比率 0 ~ 7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses                #如果应用程序引入的有jar包,并且想混淆jar包里面的class
-dontpreverify
-verbose                                        #混淆后生产映射文件 map 类名->转化后类名的映射
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*       #混淆采用的算法.

-keep public class * extends android.app.Activity                         #所有activity的子类不要去混淆
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends java.io.Serializable

-dontwarn android.support.v4.**
-dontwarn android.annotation


-keepclasseswithmembernames class * {            # 所有native的方法不能去混淆.
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);            #某些构造方法不能去混淆
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class android.content.pm.PackageInfo{*;}
-keep class android.content.pm.PackageUserState{*;}
-keep class com.android.packageinstaller.**{*;}
-keep class android.app.**{*;}
-keep class android.content.pm.**{*;}
-keep class android.os.**{*;}


-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {                                # 枚举类不能去混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {              # aidl文件不能去混淆.
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}


-keep class com.slk.daemon.NativeDaemonBase{*;}
-keep class com.slk.daemon.nativ.NativeDaemonAPI20{*;}
-keep class com.slk.daemon.nativ.NativeDaemonAPI21{*;}
-keep class com.slk.daemon.DaemonApplication{*;}
-keep class com.slk.daemon.DaemonClient{*;}
-keepattributes Exceptions,InnerClasses,...
-keep class com.slk.daemon.DaemonConfigurations{*;}
-keep class com.slk.daemon.DaemonConfigurations$*{*;}

#-keep class **{*;}
#-repackageclass com.android.phone

-ignorewarnings
