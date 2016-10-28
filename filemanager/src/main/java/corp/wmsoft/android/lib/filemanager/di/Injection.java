package corp.wmsoft.android.lib.filemanager.di;

import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.interactors.GetThumbDrawable;
import corp.wmsoft.android.lib.filemanager.interactors.OnFileObserverEvent;
import corp.wmsoft.android.lib.filemanager.interactors.UpdateListSummary;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;
import corp.wmsoft.android.lib.mvpcrx.util.MVPCSchedulerProvider;


/**
 * Created by admin on 8/10/16 at 2:58 PM
 * Enables injection
 */
public class Injection {



    // UseCases
    public static IMVPCSchedulerProvider provideMVPCSchedulerProvider() {
        return MVPCSchedulerProvider.getInstance();
    }

    public static GetFSOList provideGetFSOList() {
        return new GetFSOList(provideMVPCSchedulerProvider());
    }

    public static GetMountPoints provideGetMountPoints() {
        return new GetMountPoints(provideMVPCSchedulerProvider(), WMFileManager.getApplicationContext());
    }

    public static GetThumbDrawable provideGetThumbDrawable() {
        return new GetThumbDrawable(provideMVPCSchedulerProvider(), WMFileManager.getApplicationContext());
    }

    public static UpdateListSummary provideUpdateListSummary() {
        return new UpdateListSummary(provideMVPCSchedulerProvider(), WMFileManager.getApplicationContext());
    }

    public static OnFileObserverEvent provideOnFileObserverEvent() {
        return new OnFileObserverEvent(provideMVPCSchedulerProvider());
    }
}
