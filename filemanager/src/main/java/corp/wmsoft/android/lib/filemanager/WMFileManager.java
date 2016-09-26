package corp.wmsoft.android.lib.filemanager;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Keep;

import java.lang.ref.WeakReference;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;


/**
 * <br/>Created by WestMan2000 on 9/2/16 at 1:50 PM.<br/>
 */
public class WMFileManager {

    /**/
    private static WeakReference<Context> mWeakApplicationContext;


    @Keep
    public static void init(Application application) {
        mWeakApplicationContext = new WeakReference<>(application.getApplicationContext());

        PreferencesHelper.loadDefaults(application);
    }

    /**
     * Method that creates a {@link FileSystemObject} from a path
     *
     * @param path The path
     * @return FileSystemObject The file system object reference
     */
    @Keep
    public static FileSystemObject createFileSystemObject(String path) {
        return FileHelper.createFileSystemObject(path);
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
}