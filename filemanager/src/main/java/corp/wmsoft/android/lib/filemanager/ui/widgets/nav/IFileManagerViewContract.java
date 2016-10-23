package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.mvpcrx.presenter.IMVPCPresenter;
import corp.wmsoft.android.lib.mvpcrx.view.IMVPCView;


/**
 * This specifies the contract between the view and the presenter.
 */
public interface IFileManagerViewContract {

    interface View extends IMVPCView {

        void setViewModel(FileManagerViewModel viewModel);

        void sendEvent(@IFileManagerEvent int event);

        void onExternalStoragePermissionsGranted();

        void onExternalStoragePermissionsNotGranted();

        void showAsList();

        void showAsGrid();

        void setNavigationModeInternal(@IFileManagerNavigationMode int mode);

        void filePicked(String file);

        void directoryChanged(String dir);

        void openMountPoint(MountPoint mountPoint);

        boolean goBack();
    }

    interface Presenter extends IMVPCPresenter<View> {

        void                            changeViewMode(final @IFileManagerNavigationMode int newMode);

        @IFileManagerNavigationMode int getCurrentMode();

        @IFileManagerFileTimeFormat int getCurrentFileTimeFormat();

        void                            onExternalStoragePermissionsGranted();

        void                            onFSOPicked(FSOViewModel fsoViewModel);

        void                            openMountPoint(MountPoint mountPoint);

        void                            onSetTimeFormat(@IFileManagerFileTimeFormat int format);

        boolean                         isShowHidden();

        void                            setShowHidden(boolean isVisible);

        boolean                         isShowDirsFirst();

        void                            setShowDirsFirst(boolean isDirsFirst);

        void                            setSortMode(@IFileManagerSortMode int mode);

        @IFileManagerSortMode int       getSortMode();

        boolean                         isShowThumbs();

        void                            setShowThumbs(boolean isThumbsVisible);

        String                          getCurrentDir();

        boolean                         onGoBack();

        void                            onBreadCrumbClick(BreadCrumb breadCrumb);
    }
}