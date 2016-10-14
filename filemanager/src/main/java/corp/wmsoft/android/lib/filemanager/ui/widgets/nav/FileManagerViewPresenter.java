package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;
import corp.wmsoft.android.lib.mvpcrx.presenter.MVPCPresenter;
import rx.Subscriber;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 3:49 PM.<br/>
 */

// TODO - мега баг!
// TODO - если закрыть диалог, то FileObserver отключится, но список файорв еще старый при открытии снова, и можно выбрать не существующий файл


@SuppressLint("LongLogTag")
public class FileManagerViewPresenter extends MVPCPresenter<IFileManagerViewContract.View> implements IFileManagerViewContract.Presenter {

    /**/
    private final static String TAG = "FileManagerViewPresenter";

    /**
     * Use cases
     */
    private final GetFSOList mGetFSOList;
    private final GetMountPoints mGetMountPoints;
    /**/
    private static final int FILE_OBSERVER_MASK = FileObserver.CREATE
            | FileObserver.DELETE | FileObserver.DELETE_SELF
            | FileObserver.MOVED_FROM | FileObserver.MOVED_TO
            | FileObserver.MODIFY | FileObserver.MOVE_SELF;
    /**/
    private FileObserver mFileObserver;
    /**/
    private List<FileSystemObject> mFiles;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentMode;
    /**/
    private String mCurrentDir;
    /**
     *  we need Handler because FileObserver send events in worker thread
     */
    private Handler mHandler;


    public FileManagerViewPresenter(
            GetFSOList getFSOList,
            GetMountPoints getMountPoints) {

        this.mGetFSOList     = getFSOList;
        this.mGetMountPoints = getMountPoints;

        //Initialize variables
        this.mFiles = new ArrayList<>();

        mHandler = new Handler();
    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        super.attachView(mvpView);
        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void detachView() {
        super.detachView();

        releaseFileObserver();
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        releaseFileObserver();

    }

    @IFileManagerNavigationMode
    @Override
    public int getCurrentMode() {
        return mCurrentMode;
    }

    @IFileManagerFileTimeFormat
    @Override
    public int getCurrentFileTimeFormat() {
        return PreferencesHelper.getFileManagerFileTimeFormat();
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        //noinspection WrongConstant
        if (mCurrentMode == 0)
            setViewMode(PreferencesHelper.getFileManagerNavigationMode()); // Set the default configuration if this is first launch
        else
            setViewMode(mCurrentMode);

        if (mCurrentDir == null) {
            // get mount point manually or ask for it from MountPointsView if it set
            loadMountPoints();
        } else {
            if (mFiles == null)
                changeCurrentDir(mCurrentDir);
            else {
                showFileList();
            }
        }
    }

    @Override
    public void onFSOPicked(FileSystemObject fso) {
        if (fso instanceof ParentDirectory) {
            changeCurrentDir(fso.getParent());
        } else if (fso instanceof Directory) {
            changeCurrentDir(fso.getFullPath());
        } else {
            getView().filePicked(fso.getFullPath());
        }
    }

    @Override
    public void onSetTimeFormat(@IFileManagerFileTimeFormat int format) {

        PreferencesHelper.setFileManagerFileTimeFormat(format);

        if (mCurrentMode == IFileManagerNavigationMode.ICONS ||
            mCurrentMode == IFileManagerNavigationMode.SIMPLE)
            return;

        FileHelper.sReloadDateTimeFormats = true;
        getView().timeFormatChanged();
    }

    @Override
    public boolean isShowHidden() {
        return PreferencesHelper.isShowHidden();
    }

    @Override
    public void setShowHidden(boolean isVisible) {
        PreferencesHelper.setShowHidden(isVisible);
        loadFSOList();
    }

    @Override
    public boolean isShowDirsFirst() {
        return PreferencesHelper.isShowDirsFirst();
    }

    @Override
    public void setShowDirsFirst(boolean isDirsFirst) {
        PreferencesHelper.setShowDirsFirst(isDirsFirst);
        loadFSOList();
    }

    @Override
    public void setSortMode(@IFileManagerSortMode int mode) {
        PreferencesHelper.setFileManagerSortMode(mode);
        loadFSOList();
    }

    @Override
    public int getSortMode() {
        return PreferencesHelper.getFileManagerSortMode();
    }

    @Override
    public boolean isShowThumbs() {
        return PreferencesHelper.isShowThumbs();
    }

    @Override
    public void setShowThumbs(boolean isThumbsVisible) {
        PreferencesHelper.setShowThumbs(isThumbsVisible);
        loadFSOList();
    }

    @Override
    public String getCurrentDir() {
        return mCurrentDir;
    }

    /**
     * Method that change the view mode.
     *
     * @param newMode The new mode
     */
    @Override
    public void changeViewMode(final @IFileManagerNavigationMode int newMode) {

        if (mCurrentMode == newMode) return;

        if (newMode == IFileManagerNavigationMode.ICONS) {
            getView().showAsGrid();
        } else {
            //noinspection WrongConstant
            if (mCurrentMode == IFileManagerNavigationMode.ICONS || mCurrentMode == 0)
                getView().showAsList();
        }

        this.mCurrentMode = newMode;

        getView().setNavigationModeInternal(mCurrentMode);
        PreferencesHelper.setFileManagerNavigationMode(mCurrentMode);
    }

    private void setViewMode(final @IFileManagerNavigationMode int newMode) {

        if (newMode == IFileManagerNavigationMode.ICONS) {
            getView().showAsGrid();
        } else {
            getView().showAsList();
        }

        this.mCurrentMode = newMode;

        getView().setNavigationModeInternal(mCurrentMode);
    }

    /**
     * Method that changes the current directory of the view.
     *
     * @param newDir The new directory location
     */
    private void changeCurrentDir(final String newDir) {
        this.mCurrentDir = newDir;
        loadFSOList();
    }

    private void loadMountPoints() {

        executeUseCase(mGetMountPoints, new GetMountPoints.RequestValues(true), new Subscriber<List<MountPoint>>() {
            @Override
            public void onCompleted() {
                loadFSOList();
            }

            @Override
            public void onError(Throwable e) {
                getView().showError(new Error("Can't get mount points"));
                mCurrentDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            }

            @Override
            public void onNext(List<MountPoint> mountPoints) {
                if (mountPoints.size() == 0) {
                    mCurrentDir = Environment.getExternalStorageDirectory().getAbsolutePath();
                    return;
                }
                // get first available mount point
                mCurrentDir = mountPoints.get(0).getPath();
            }
        });
    }

    private void loadFSOList() {
        getView().showLoading();
        executeUseCase(mGetFSOList, new GetFSOList.RequestValues(mCurrentDir), new Subscriber<List<FileSystemObject>>() {
            @Override
            public void onCompleted() {
                showFileList();
                FileSystemObject dir = FileHelper.createFileSystemObject(new File(mCurrentDir));
                getView().directoryChanged(dir.getFullPath());
            }

            @Override
            public void onError(Throwable e) {
                getView().showError(new Error(e));
            }

            @Override
            public void onNext(List<FileSystemObject> fileSystemObjects) {
                mFiles = new ArrayList<>(fileSystemObjects);
            }
        });
    }

    private void showFileList() {

        getView().setData(mFiles);

        releaseFileObserver();
        mFileObserver = new FileObserver(mCurrentDir, FILE_OBSERVER_MASK) {
            @Override
            public void onEvent(int event, String path) {
                int newEvent = event & FileObserver.ALL_EVENTS;
                if (path != null && newEvent > 0)
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadFSOList();
                        }
                    });
            }
        };
        mFileObserver.startWatching();

        // TODO - Add to history?
        // TODO - Change the breadcrumb
        // TODO - The current directory is now the "newDir"

        getView().showContent();
        getView().hideLoading();
    }

    private void releaseFileObserver() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }
}
