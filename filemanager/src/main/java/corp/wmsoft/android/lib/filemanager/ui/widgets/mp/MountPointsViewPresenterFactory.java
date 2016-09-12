package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import corp.wmsoft.android.lib.filemanager.di.Injection;
import corp.wmsoft.android.lib.mvpc.presenter.factory.IMVPCPresenterFactory;

/**
 * Created by WestMan2000 on 9/11/16. <br/>
 */
public class MountPointsViewPresenterFactory implements IMVPCPresenterFactory<IMountPointsViewContract.View, IMountPointsViewContract.Presenter> {


    public MountPointsViewPresenterFactory() {
    }

    @Override
    public IMountPointsViewContract.Presenter create() {
        return new MountPointsViewPresenter(
                Injection.provideGetMountPoints()
        );
    }
}
