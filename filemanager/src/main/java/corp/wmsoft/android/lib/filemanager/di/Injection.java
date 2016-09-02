package corp.wmsoft.android.lib.filemanager.di;

import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.mvpc.util.IMVPCSchedulerProvider;
import corp.wmsoft.android.lib.mvpc.util.MVPCSchedulerProvider;


/**
 * Created by admin on 8/10/16 at 2:58 PM
 * Enables injection
 */
public class Injection {


    /***********************************************************
     * UseCases
     */
    public static IMVPCSchedulerProvider provideMVPCSchedulerProvider() {
        return MVPCSchedulerProvider.getInstance();
    }

    public static GetFSOList provideGetFSOList() {
        return new GetFSOList(provideMVPCSchedulerProvider());
    }

}
