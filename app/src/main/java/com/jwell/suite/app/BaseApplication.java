package com.jwell.suite.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class BaseApplication extends Application {

    public static boolean DEBUG = false;
    public static String TAG = "BASE APPLICATION";


    @Override
    public void onCreate() {
        super.onCreate();
        this.DEBUG = isDebuggable(this);
    }
    /**
     * get Debug Mode
     *
     * @param context
     * @return
     */
    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            //throw new RuntimeException(e);
            Log.i(TAG,"DEBUG MODE PACK NAME NOT FOUND .. LOG will not WORK ");
        }catch (Exception  e) {
            /* debuggable variable will remain false */
            Log.i(TAG,"DEBUG MODE IDENTIFY FAILED ");
        }

        return debuggable;
    }

}
