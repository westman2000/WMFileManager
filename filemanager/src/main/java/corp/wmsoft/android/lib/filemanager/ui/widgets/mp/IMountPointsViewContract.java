package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import android.support.annotation.Keep;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.mvpcrx.presenter.IMVPCPresenter;
import corp.wmsoft.android.lib.mvpcrx.view.IMVPCDelayedDataView;


/**
 * This specifies the contract between the view and the presenter.
 */
interface IMountPointsViewContract {

    @Keep
    interface View extends IMVPCDelayedDataView<List<MountPoint>> {

        void sendEvent(@IFileManagerEvent int event);

        void onExternalStoragePermissionsGranted();

        void onExternalStoragePermissionsNotGranted();

        void selectMountPoint(MountPoint mountPoint, boolean fireEvent);

    }

    @Keep
    interface Presenter extends IMVPCPresenter<View> {

        void onExternalStoragePermissionsGranted();

        void onSelectMountPoint(int mountPointId);

    }
}