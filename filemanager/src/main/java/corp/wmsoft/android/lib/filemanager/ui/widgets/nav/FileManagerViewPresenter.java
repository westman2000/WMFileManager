package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;
import corp.wmsoft.android.lib.mvpc.presenter.MVPCPresenter;
import rx.Subscriber;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 3:49 PM.<br/>
 */
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
    private List<FileSystemObject> mFiles;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentMode;
    /**/
    private String mCurrentDir;


    public FileManagerViewPresenter(
            GetFSOList getFSOList,
            GetMountPoints getMountPoints) {

        this.mGetFSOList     = getFSOList;
        this.mGetMountPoints = getMountPoints;

        //Initialize variables
        this.mFiles = new ArrayList<>();
    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        super.attachView(mvpView);
        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
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

        // TODO - Add to history?
        // TODO - Change the breadcrumb
        // TODO - The current directory is now the "newDir"

        getView().showContent();
        getView().hideLoading();
    }

}
