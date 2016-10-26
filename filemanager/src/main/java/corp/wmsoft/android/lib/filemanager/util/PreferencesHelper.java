package corp.wmsoft.android.lib.filemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.WMFileManager;


/**
 * <br/>Created by WestMan2000 on 9/6/16 at 3:49 PM.<br/>
 * A helper class for access and manage the preferences of the application.
 */
public class PreferencesHelper {

    /**/
    private static final String TAG = "wmfm:PreferencesHelper";

    /**
     * The name of the file manager settings file.
     * @hide
     */
    public static final String SETTINGS_FILENAME = "corp.wmsoft.android.lib.filemanager";


    /**
     * Method that returns the shared preferences of the application.
     *
     * @return SharedPreferences The shared preferences of the application
     * @hide
     */
    public static SharedPreferences getSharedPreferences() {
        return WMFileManager.getApplicationContext().getSharedPreferences(SETTINGS_FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * Method that initializes the defaults preferences of the application.
     */
    public static void loadDefaults(Context context) {
        // Setting Default Values before start
        PreferenceManager.setDefaultValues(context, SETTINGS_FILENAME, Context.MODE_PRIVATE, R.xml.file_manager_settings, false);
    }

    @IFileManagerNavigationMode
    public static int getFileManagerNavigationMode() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_navigation_mode);
        //noinspection WrongConstant
        @IFileManagerNavigationMode int mode = getSharedPreferences().getInt(key, IFileManagerNavigationMode.DETAILS);
        return mode;
    }

    public static void setFileManagerNavigationMode(@IFileManagerNavigationMode int mode) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_navigation_mode);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putInt(key, mode);
        //Commit settings
        editor.apply();
    }

    @IFileManagerFileTimeFormat
    public static int getFileManagerFileTimeFormat() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_filetime_format);
        //noinspection WrongConstant
        @IFileManagerFileTimeFormat int format = getSharedPreferences().getInt(key, IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS);
        return format;
    }

    public static void setFileManagerFileTimeFormat(@IFileManagerFileTimeFormat int format) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_filetime_format);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putInt(key, format);
        //Commit settings
        editor.apply();
    }

    public static boolean isShowDirsFirst() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_dirs_first);
        return getSharedPreferences().getBoolean(key, true);
    }

    public static void setShowDirsFirst(boolean isShowDirsFirst) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_dirs_first);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putBoolean(key, isShowDirsFirst);
        //Commit settings
        editor.apply();
    }

    public static boolean isShowHidden() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_hidden);
        return getSharedPreferences().getBoolean(key, false);
    }

    public static void setShowHidden(boolean isShowHidden) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_hidden);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putBoolean(key, isShowHidden);
        //Commit settings
        editor.apply();
    }

    @IFileManagerSortMode
    public static int getFileManagerSortMode() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_sort_mode);
        //noinspection WrongConstant
        @IFileManagerSortMode int sortMode = getSharedPreferences().getInt(key, IFileManagerSortMode.NAME_ASC);
        return sortMode;
    }

    public static void setFileManagerSortMode(@IFileManagerSortMode int sortMode) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_sort_mode);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putInt(key, sortMode);
        //Commit settings
        editor.apply();
    }

    public static boolean isCaseSensitiveSort() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_case_sensitive);
        return getSharedPreferences().getBoolean(key, false);
    }

    public static void setCaseSensitiveSort(boolean isCaseSensitiveSort) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_case_sensitive);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putBoolean(key, isCaseSensitiveSort);
        //Commit settings
        editor.apply();
    }

    public static boolean isShowThumbs() {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_thumbs);
        return getSharedPreferences().getBoolean(key, true);
    }

    public static void setShowThumbs(boolean isShowThumbs) {
        String key = WMFileManager.getApplicationContext().getString(R.string.wm_fm_pref_key_show_thumbs);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putBoolean(key, isShowThumbs);
        //Commit settings
        editor.apply();

        IconsHelper.cleanup();
    }
}