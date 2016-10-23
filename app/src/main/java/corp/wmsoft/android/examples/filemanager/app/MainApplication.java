package corp.wmsoft.android.examples.filemanager.app;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import corp.wmsoft.android.lib.filemanager.WMFileManager;


/**
 * Created by westman on 8/6/16.
 *
 */
public class MainApplication extends Application {


    @Override public void onCreate() {
        super.onCreate();

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        WMFileManager.init(this);
    }

}