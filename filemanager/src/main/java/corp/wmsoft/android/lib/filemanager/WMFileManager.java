package corp.wmsoft.android.lib.filemanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
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
     */
    public static void showAsDirectoryChooser(Activity activity) {

        // set restrictions
        setRestrictionOnlyDirectory();

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_DIRECTORY_ONLY);
        activity.startActivityForResult(intent, FileManagerActivity.REQUEST_CODE);
    }

    /**
     * Show dialog "Save As...". Implement {@link Activity#onActivityResult(int, int, Intent)} to get result
     * @param activity activity to handle result
     * @param defaultFileName default file name
     */
    public static void showFileSaveAsDialog(Activity activity, @Nullable String defaultFileName) {

        // set restrictions
        setRestrictionOnlyDirectory();

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_SAVE_FILE);
        intent.putExtra(FileManagerActivity.EXTRA_DEFAULT_FILE_NAME, defaultFileName);
        activity.startActivityForResult(intent, FileManagerActivity.REQUEST_CODE);
    }

    /**
     * Show dialog to pick file. Implement {@link Activity#onActivityResult(int, int, Intent)} to get result
     * @param activity activity
     * @param mimeTypes show only this file types
     */
    public static void showAsFilePicker(Activity activity, @Nullable String... mimeTypes) {

        // set restrictions
        setRestrictionForMimeTypes(mimeTypes);

        Intent intent = new Intent(activity, FileManagerActivity.class);
        intent.putExtra(FileManagerActivity.EXTRA_TYPE, FileManagerActivity.TYPE_FILE_PICKER);
        activity.startActivityForResult(intent, FileManagerActivity.REQUEST_CODE);
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