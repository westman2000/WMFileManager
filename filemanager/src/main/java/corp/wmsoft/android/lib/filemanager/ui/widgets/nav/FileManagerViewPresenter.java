package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
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
        Log.d(TAG, "FileManagerViewPresenter.FileManagerViewPresenter()");

        this.mGetFSOList     = getFSOList;
        this.mGetMountPoints = getMountPoints;

        //Initialize variables
        this.mFiles = new ArrayList<>();

        mCurrentMode = IFileManagerNavigationMode.DETAILS;


    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        Log.d(TAG, "FileManagerViewPresenter.attachView()");
        super.attachView(mvpView);
        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
    }

    @IFileManagerNavigationMode
    public int getCurrentMode() {
        return mCurrentMode;
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        Log.d(TAG, "FileManagerViewPresenter.onExternalStoragePermissionsGranted()");
        // Retrieve the default configuration
        changeViewMode(mCurrentMode);

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
        Log.d(TAG, "FileManagerViewPresenter.onFSOPicked("+fso+")");
        if (fso instanceof ParentDirectory) {
            changeCurrentDir(fso.getParent());
        } else if (fso instanceof Directory) {
            changeCurrentDir(fso.getFullPath());
        }
    }

    /**
     * Method that change the view mode.
     *
     * @param newMode The new mode
     */
    @Override
    public void changeViewMode(final @IFileManagerNavigationMode int newMode) {
        Log.d(TAG, "FileManagerViewPresenter.changeViewMode("+newMode+")");

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
        Log.d(TAG, "FileManagerViewPresenter.changeCurrentDir("+newDir+")");
        this.mCurrentDir = newDir;

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

    private void loadMountPoints() {
        Log.d(TAG, "FileManagerViewPresenter.loadMountPoints()");

        executeUseCase(mGetMountPoints, new GetMountPoints.RequestValues(true), new Subscriber<List<MountPoint>>() {
            @Override
            public void onCompleted() {
                changeCurrentDir(mCurrentDir);
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

    private void showFileList() {
        Log.d(TAG, "FileManagerViewPresenter.showFileList()");

        getView().setData(mFiles);

        // TODO - Add to history?
        // TODO - Change the breadcrumb
        // TODO - The current directory is now the "newDir"

        getView().showContent();
        getView().hideLoading();
    }

}
