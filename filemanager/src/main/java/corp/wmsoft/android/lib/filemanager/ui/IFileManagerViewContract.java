package corp.wmsoft.android.lib.filemanager.ui;

import android.support.annotation.Keep;

import java.util.ArrayList;
import java.util.List;

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

        /**
         * will fire this event to check permission to external storage
         */
        void askForExternalStoragePermission();

        void setViewModel(FileManagerViewModel viewModel);

        void showAsList();

        void showAsGrid();

        void setNavigationModeInternal(@IFileManagerNavigationMode int mode);

        void filePicked(String file);

        void directoryChanged(String dir);

        void selectMountPoint(MountPoint mountPoint);

        void showCreateNewFolderView(ArrayList<String> currentPathFolders);
    }

    interface Presenter extends IMVPCPresenter<View> {

        void                            changeViewMode(final @IFileManagerNavigationMode int newMode);

        @IFileManagerNavigationMode int getCurrentMode();

        @IFileManagerFileTimeFormat int getCurrentFileTimeFormat();

        void                            onExternalStoragePermissionsGranted();

        void                            onExternalStoragePermissionsNotGranted();

        @Keep
        void                            onFSOPicked(FSOViewModel fsoViewModel);

        void                            onMountPointSelect(MountPoint mountPoint);

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

        /**
         * Go to parent folder
         * @return true if was handled, moved to previous path, false otherwise
         */
        boolean                         onGoBack();

        @Keep
        void                            onBreadCrumbClick(BreadCrumb breadCrumb);

        void                            onCreateNewFolder();

        void                            onNewFolderCreated(String folderName);
    }
}