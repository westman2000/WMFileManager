package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.mvpc.presenter.MVPCPresenter;
import hugo.weaving.DebugLog;
import rx.Subscriber;

/**
 * <br/>Created by WestMan2000 on 8/31/16 at 3:49 PM.<br/>
 */
@DebugLog
@SuppressLint("LongLogTag")
public class FileManagerViewPresenter extends MVPCPresenter<IFileManagerViewContract.View> implements IFileManagerViewContract.Presenter {

    /**/
    private final static String TAG = "FileManagerViewPresenter";

    /**
     * Use cases
     */
    private final GetFSOList mGetFSOList;
    /**/
    private List<FileSystemObject> mFiles;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentMode;
    /**/
    private String mCurrentDir;


    public FileManagerViewPresenter(GetFSOList getFSOList) {
        Log.d(TAG, "FileManagerViewPresenter.FileManagerViewPresenter()");

        this.mGetFSOList = getFSOList;

        //Initialize variables
        this.mFiles = new ArrayList<>();
    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        Log.d(TAG, "FileManagerViewPresenter.attachView()");
        super.attachView(mvpView);
        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        Log.d(TAG, "FileManagerViewPresenter.onExternalStoragePermissionsGranted()");
        // Retrieve the default configuration
        changeViewMode(IFileManagerNavigationMode.DETAILS);

        changeCurrentDir(Environment.getExternalStorageDirectory().getAbsolutePath());
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
                getView().showContent();
                getView().hideLoading();
            }

            @Override
            public void onError(Throwable e) {
                getView().showError(new Error(e));
            }

            @Override
            public void onNext(List<FileSystemObject> fileSystemObjects) {
                getView().setData(fileSystemObjects);

            }
        });

    }

    /**
     * Method that change the view mode.
     *
     * @param newMode The new mode
     */
    public void changeViewMode(final @IFileManagerNavigationMode int newMode) {
        Log.d(TAG, "FileManagerViewPresenter.changeViewMode("+newMode+")");
//        Check that it is really necessary change the mode
//        if (this.mCurrentMode == newMode) return;

        if (newMode == IFileManagerNavigationMode.ICONS || newMode == IFileManagerNavigationMode.SIMPLE) {
            getView().showAsGrid();
        } else {
            getView().showAsList();
        }

        this.mCurrentMode = newMode;
    }


}
