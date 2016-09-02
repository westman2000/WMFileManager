package corp.wmsoft.android.lib.filemanager.ui.widgets;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.NavigationMode;
import corp.wmsoft.android.lib.mvpc.presenter.MVPCPresenter;
import hugo.weaving.DebugLog;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * <br/>Created by WestMan2000 on 8/31/16 at 3:49 PM.<br/>
 */
@DebugLog
public class NavigationViewPresenter extends MVPCPresenter<INavigationViewContract.View> implements INavigationViewContract.Presenter {

    /**
     * Use cases
     */
    private final GetFSOList mGetFSOList;
    /**/
    private List<FileSystemObject> mFiles;
    /**/
    @NavigationMode
    private int mCurrentMode;
    /**/
    private String mCurrentDir;


    public NavigationViewPresenter(GetFSOList getFSOList) {
        this.mGetFSOList = getFSOList;

        //Initialize variables
        this.mFiles = new ArrayList<>();
    }

    @Override
    public void attachView(INavigationViewContract.View mvpView) {
        super.attachView(mvpView);

        // Retrieve the default configuration
        changeViewMode(NavigationMode.DETAILS);

        changeCurrentDir(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    @Override
    public void onFSOPicked(FileSystemObject fso) {
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
        this.mCurrentDir = newDir;

        getView().showLoading();
        executeUseCase(mGetFSOList, new GetFSOList.RequestValues(mCurrentDir), new Subscriber<List<FileSystemObject>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<FileSystemObject> fileSystemObjects) {
                getView().setData(fileSystemObjects);
                getView().showContent();
                getView().hideLoading();
            }
        });

    }

    /**
     * Method that change the view mode.
     *
     * @param newMode The new mode
     */
    public void changeViewMode(final @NavigationMode int newMode) {
        //Check that it is really necessary change the mode
        if (this.mCurrentMode == newMode) return;

        if (newMode == NavigationMode.ICONS || newMode == NavigationMode.SIMPLE) {
            getView().showAsGrid();
        } else {
            getView().showAsList();
        }

        this.mCurrentMode = newMode;
    }


}
