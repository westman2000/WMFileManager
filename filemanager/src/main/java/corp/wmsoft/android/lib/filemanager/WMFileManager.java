package corp.wmsoft.android.lib.filemanager;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;


/**
 * <br/>Created by WestMan2000 on 9/2/16 at 1:50 PM.<br/>
 */
public class WMFileManager {

    /**/
    private static WeakReference<Context> mWeakApplicationContext;


    public static void init(Application application) {
        mWeakApplicationContext = new WeakReference<>(application.getApplicationContext());

        PreferencesHelper.loadDefaults(application);
    }

    public static Context getApplicationContext() {
        if (mWeakApplicationContext == null || mWeakApplicationContext.get() == null)
            throw new RuntimeException("WMFileManager not initialized! Add WMFileManager.init(Context) to Application onCreate()");

        return mWeakApplicationContext.get();
    }
}