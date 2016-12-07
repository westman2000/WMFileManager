package corp.wmsoft.android.lib.filemanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;

import java.lang.ref.WeakReference;

import corp.wmsoft.android.lib.filemanager.ui.FileManagerActivity;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
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
     * Show dialog to choose folder. Implement {@link Activity#onActivityResult(int, int, Intent)} to get result
     * @param activity activity
     * @param requestCode request code
     */
    public static void showAsDirectoryChooser(Activity activity, int requestCode) {

        // set restrictions
        setRestrictionOnlyDirectory();

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_DIRECTORY_ONLY);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Show dialog to choose folder. Implement {@link Fragment#onActivityResult(int, int, Intent)} to get result
     * @param fragment fragment
     * @param requestCode request code
     */
    public static void showAsDirectoryChooser(Fragment fragment, int requestCode) {

        // set restrictions
        setRestrictionOnlyDirectory();

        Intent intent = new Intent(fragment.getContext(), FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_DIRECTORY_ONLY);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Show dialog "Save As...". Implement {@link Activity#onActivityResult(int, int, Intent)} to get result
     * @param activity activity to handle result
     * @param requestCode request code
     * @param defaultFileName default file name
     */
    public static void showFileSaveAsDialog(Activity activity, int requestCode, @Nullable String defaultFileName) {

        // set restrictions
        setRestrictionForMimeTypes((String) null);

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_SAVE_FILE);
        intent.putExtra(FileManagerActivity.EXTRA_DEFAULT_FILE_NAME, defaultFileName);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Show dialog "Save As...". Implement {@link Fragment#onActivityResult(int, int, Intent)} to get result
     * @param fragment fragment to handle result
     * @param requestCode request code
     * @param defaultFileName default file name
     */
    public static void showFileSaveAsDialog(Fragment fragment, int requestCode, @Nullable String defaultFileName) {

        // set restrictions
        setRestrictionForMimeTypes((String) null);

        Intent intent = new Intent(fragment.getContext(), FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_SAVE_FILE);
        intent.putExtra(FileManagerActivity.EXTRA_DEFAULT_FILE_NAME, defaultFileName);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * Show dialog to pick file. Implement {@link Activity#onActivityResult(int, int, Intent)} to get result
     * @param activity activity
     * @param requestCode request code
     * @param mimeTypes show only this file types
     */
    public static void showAsFilePicker(Activity activity, int requestCode, @Nullable String... mimeTypes) {

        // set restrictions
        setRestrictionForMimeTypes(mimeTypes);

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_FILE_PICKER);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * Show dialog to pick file. Implement {@link Fragment#onActivityResult(int, int, Intent)} to get result
     * @param fragment fragment
     * @param requestCode request code
     * @param mimeTypes show only this file types
     */
    public static void showAsFilePicker(Fragment fragment, int requestCode, @Nullable String... mimeTypes) {

        // set restrictions
        setRestrictionForMimeTypes(mimeTypes);

        Intent intent = new Intent(fragment.getContext(), FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_FILE_PICKER);
        fragment.startActivityForResult(intent, requestCode);
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

    private static void setRestrictionOnlyDirectory() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION, true);
        FileHelper.setRestrictions(restrictions);
    }

    // "application/x-bittorrent"
    // "tox/x-profile"
    private static void setRestrictionForMimeTypes(@Nullable String... mimeTypes) {

        SparseArray<Object> restrictions = new SparseArray<>();

        if (mimeTypes == null) {
            FileHelper.setRestrictions(restrictions);
            return;
        }

        for (String mimeType : mimeTypes) {
            restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, mimeType);
        }

        FileHelper.setRestrictions(restrictions);
    }

    private static SparseArray createRestrictionForMimeTypeCategory(MimeTypeHelper.MimeTypeCategory mimeTypeCategory) {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION, mimeTypeCategory);
        return restrictions;
    }
}