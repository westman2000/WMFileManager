package corp.wmsoft.android.examples.filemanager.app;

import android.app.Application;

import corp.wmsoft.android.lib.filemanager.WMFileManager;


/**
 * Created by westman on 8/6/16.
 *
 */
public class MainApplication extends Application {


    @Override public void onCreate() {
        super.onCreate();

        WMFileManager.init(this);
    }

}