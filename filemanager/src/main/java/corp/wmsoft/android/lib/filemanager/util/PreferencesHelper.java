package corp.wmsoft.android.lib.filemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.WMFileManager;

/**
 * <br/>Created by WestMan2000 on 9/6/16 at 3:49 PM.<br/>
 * A helper class for access and manage the preferences of the application.
 */
public class PreferencesHelper {

    /**/
    private static final String TAG = "PreferencesHelper";

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
        String key = WMFileManager.getApplicationContext().getString(R.string.pref_key_navigation_mode);
        //noinspection WrongConstant
        return getSharedPreferences().getInt(key, IFileManagerNavigationMode.ICONS);
    }

    public static void setFileManagerNavigationMode(@IFileManagerNavigationMode int mode) {
        String key = WMFileManager.getApplicationContext().getString(R.string.pref_key_navigation_mode);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putInt(key, mode);
        //Commit settings
        editor.apply();
    }

    @IFileManagerFileTimeFormat
    public static int getFileManagerFileTimeFormat() {
        String key = WMFileManager.getApplicationContext().getString(R.string.pref_key_filetime_format);
        //noinspection WrongConstant
        return getSharedPreferences().getInt(key, IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS);
    }

    public static void setFileManagerFileTimeFormat(@IFileManagerFileTimeFormat int format) {
        String key = WMFileManager.getApplicationContext().getString(R.string.pref_key_filetime_format);
        //Get the preferences editor
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        //Save
        editor.putInt(key, format);
        //Commit settings
        editor.apply();
    }
}