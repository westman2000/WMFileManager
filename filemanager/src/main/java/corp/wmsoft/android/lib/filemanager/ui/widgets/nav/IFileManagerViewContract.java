package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.mvpc.presenter.IMVPCPresenter;
import corp.wmsoft.android.lib.mvpc.view.IMVPCDelayedDataView;


/**
 * This specifies the contract between the view and the presenter.
 */
public interface IFileManagerViewContract {

    interface View extends IMVPCDelayedDataView<List<FileSystemObject>> {

        void sendEvent(@IFileManagerEvent int event);

        void onExternalStoragePermissionsGranted();

        void onExternalStoragePermissionsNotGranted();

        void showAsList();

        void showAsGrid();

        void setNavigationMode(@IFileManagerNavigationMode int mode);

    }

    interface Presenter extends IMVPCPresenter<View> {

        void onExternalStoragePermissionsGranted();

        void onFSOPicked(FileSystemObject fso);

    }
}