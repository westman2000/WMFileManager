package corp.wmsoft.android.lib.filemanager.ui.widgets;

import corp.wmsoft.android.lib.filemanager.di.Injection;
import corp.wmsoft.android.lib.mvpc.presenter.factory.IMVPCPresenterFactory;


/**
 * Created by admin on 8/8/16 at 3:53 PM
 *
 */
public class NavigationViewPresenterFactory implements IMVPCPresenterFactory<INavigationViewContract.View, INavigationViewContract.Presenter> {


    public NavigationViewPresenterFactory() {
    }

    @Override
    public INavigationViewContract.Presenter create() {
        return new NavigationViewPresenter(
                Injection.provideGetFSOList()
        );
    }
}