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

    public static void setRestrictions(SparseArray mRestrictions) {
        WMFileManager.mRestrictions = mRestrictions.clone();
    }

    public static void replaceInFragmentManager(FragmentManager fragmentManager, @IdRes int containerId) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.replace(containerId, newInstance(), FileManagerFragment.TAG)
                .commit();
    }

    public static void showAsDialog(FragmentManager fragmentManager) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = newInstance();
        newFragment.show(ft, TAG);
    }

    private static FileManagerFragment newInstance() {
        FileManagerFragment frag = new FileManagerFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public static SparseArray createRestrictionOnlyDirectory() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION, true);
        return restrictions;
    }

    public static SparseArray createRestrictionOnlyImages() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION, MimeTypeHelper.MimeTypeCategory.IMAGE);
        return restrictions;
    }

    public static SparseArray createRestrictionOnlyTorrents() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, "application/x-bittorrent");
        return restrictions;
    }

    public static SparseArray createRestrictionOnlyTox() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, "tox/x-profile");
        return restrictions;
    }

    public static SparseArray getRestrictions() {
        return mRestrictions;
    }
}