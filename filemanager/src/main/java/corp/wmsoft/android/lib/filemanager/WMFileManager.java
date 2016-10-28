package corp.wmsoft.android.lib.filemanager;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Keep;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerFragment;
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

        // TODO - move to other thread
        PreferencesHelper.loadDefaults(application);

        //Force the load of mime types
        try {
            MimeTypeHelper.loadMimeTypes(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Mime-types failed.", e);
        }

        // Initialize default restrictions (no restrictions)
        mRestrictions = new SparseArray();
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

    @Keep
    public static void showDialogChooseFolder(FragmentManager fragmentManager) {
        // set restrictions
        setRestrictions(createRestrictionOnlyDirectory());

        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(FileManagerFragment.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = FileManagerFragment.newInstance();
        newFragment.show(ft, FileManagerFragment.TAG);
    }

    public static SparseArray getRestrictions() {
        return mRestrictions;
    }

    static void setRestrictions(SparseArray mRestrictions) {
        WMFileManager.mRestrictions = mRestrictions.clone();
    }

    static SparseArray createRestrictionOnlyDirectory() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION, true);
        return restrictions;
    }

    static SparseArray createRestrictionForMimeTypeCategory(MimeTypeHelper.MimeTypeCategory mimeTypeCategory) {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION, mimeTypeCategory);
        return restrictions;
    }

    // "application/x-bittorrent"
    // "tox/x-profile"
    static SparseArray createRestrictionForMimeType(String mimeType) {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, mimeType);
        return restrictions;
    }

}