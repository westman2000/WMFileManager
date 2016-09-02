package corp.wmsoft.android.examples.filemanager.app;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import corp.wmsoft.android.lib.filemanager.WMFileManager;


/**
 * Created by westman on 8/6/16.
 *
 */
public class MainApplication extends Application {

    /**/
    private RefWatcher refWatcher;


    @Override public void onCreate() {
        super.onCreate();

        WMFileManager.init(this);

        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }
}