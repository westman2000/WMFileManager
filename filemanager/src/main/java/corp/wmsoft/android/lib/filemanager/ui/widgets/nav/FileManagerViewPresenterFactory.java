package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import corp.wmsoft.android.lib.filemanager.di.Injection;
import corp.wmsoft.android.lib.mvpc.presenter.factory.IMVPCPresenterFactory;


/**
 * Created by admin on 8/8/16 at 3:53 PM
 *
 */
public class FileManagerViewPresenterFactory implements IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> {


    public FileManagerViewPresenterFactory() {
    }

    @Override
    public IFileManagerViewContract.Presenter create() {
        return new FileManagerViewPresenter(
                Injection.provideGetFSOList(),
                Injection.provideGetMountPoints()
        );
    }
}