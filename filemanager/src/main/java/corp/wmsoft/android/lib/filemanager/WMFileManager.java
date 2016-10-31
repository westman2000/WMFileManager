package corp.wmsoft.android.lib.filemanager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Keep;
import android.util.Log;

import java.lang.ref.WeakReference;

import corp.wmsoft.android.lib.filemanager.util.MimeTypeHelper;
import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;


/**
 * <br/>Created by WestMan2000 on 9/2/16 at 1:50 PM.<br/>
 */
public class WMFileManager {

    /**/
    private static final String TAG = "WMFileManager";

    /**/
    private static WeakReference<Context> mWeakApplicationContext;


    @Keep
    public static void init(Application application) {

        mWeakApplicationContext = new WeakReference<>(application.getApplicationContext());

        // TODO - move to other thread
        PreferencesHelper.loadDefaults(application);

        //Force the load of mime types
        try {
            MimeTypeHelper.loadMimeTypes(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Mime-types failed.", e);
        }

    }

    /**
     * Get application context
     * @return application context
     */
    public static Context getApplicationContext() {
        if (mWeakApplicationContext == null || mWeakApplicationContext.get() == null)
            throw new RuntimeException("WMFileManager not initialized! Add WMFileManager.init(Context) to Application onCreate()");

        return mWeakApplicationContext.get();
    }

}