package corp.wmsoft.android.lib.filemanager.ui.widgets;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.NavigationMode;
import corp.wmsoft.android.lib.mvpc.presenter.IMVPCPresenter;
import corp.wmsoft.android.lib.mvpc.view.IMVPCDelayedDataView;


/**
 * This specifies the contract between the view and the presenter.
 */
public interface INavigationViewContract {

    interface View extends IMVPCDelayedDataView<List<FileSystemObject>> {

        void showAsList();

        void showAsGrid();

        void setNavigationMode(@NavigationMode int mode);

    }

    interface Presenter extends IMVPCPresenter<View> {

        void onFSOPicked(FileSystemObject fso);

    }
}