package corp.wmsoft.android.lib.filemanager.ui;

import corp.wmsoft.android.lib.filemanager.di.Injection;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 * Created by admin on 8/8/16 at 3:53 PM
 *
 */
class FileManagerViewPresenterFactory implements IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> {


    FileManagerViewPresenterFactory() {
    }

    @Override
    public IFileManagerViewContract.Presenter create() {
        return new FileManagerViewPresenter(
                Injection.provideGetFSOList(),
                Injection.provideGetMountPoints(),
                Injection.provideUpdateListSummary(),
                Injection.provideOnFileObserverEvent()
        );
    }
}