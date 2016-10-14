package corp.wmsoft.android.lib.filemanager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.util.SparseArray;

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

    /**
     * Restrictions
     */
    private static SparseArray mRestrictions;


    @Keep
    public static void init(Application application) {
        mWeakApplicationContext = new WeakReference<>(application.getApplicationContext());

        PreferencesHelper.loadDefaults(application);

        //Force the load of mime types
        try {
            MimeTypeHelper.loadMimeTypes(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Mime-types failed.", e);
        }

        // Initialize default restrictions (no restrictions)
        mRestrictions = new SparseArray(5);
    }

    /**
     * @exclude
     * Get application context
     * @return application context
     */
    public static Context getApplicationContext() {
        if (mWeakApplicationContext == null || mWeakApplicationContext.get() == null)
            throw new RuntimeException("WMFileManager not initialized! Add WMFileManager.init(Context) to Application onCreate()");

        return mWeakApplicationContext.get();
    }

    public static void setRestrictions(SparseArray mRestrictions) {
        WMFileManager.mRestrictions = mRestrictions.clone();
    }

    public static SparseArray getRestrictions() {
        return mRestrictions;
    }

    @Keep
    public static SparseArray createRestrictionOnlyDirectory() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION, true);
        return restrictions;
    }

    @Keep
    public static SparseArray createRestrictionOnlyImages() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION, MimeTypeHelper.MimeTypeCategory.IMAGE);
        return restrictions;
    }

    @Keep
    public static SparseArray createRestrictionOnlyTorrents() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, "application/x-bittorrent");
        return restrictions;
    }

    @Keep
    public static SparseArray createRestrictionOnlyTox() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, "tox/x-profile");
        return restrictions;
    }
}