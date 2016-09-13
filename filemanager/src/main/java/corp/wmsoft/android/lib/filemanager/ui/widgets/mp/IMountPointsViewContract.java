package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.mvpc.presenter.IMVPCPresenter;
import corp.wmsoft.android.lib.mvpc.view.IMVPCDelayedDataView;


/**
 * This specifies the contract between the view and the presenter.
 */
public interface IMountPointsViewContract {

    interface View extends IMVPCDelayedDataView<List<MountPoint>> {

        void sendEvent(@IFileManagerEvent int event);

        void onExternalStoragePermissionsGranted();

        void onExternalStoragePermissionsNotGranted();

        void selectMountPoint(MountPoint mountPoint, boolean fireEvent);

    }

    interface Presenter extends IMVPCPresenter<View> {

        void onExternalStoragePermissionsGranted();

        void onSelectMountPoint(int mountPointId);

    }
}