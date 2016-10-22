package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.annotation.SuppressLint;
import android.databinding.ObservableList;
import android.os.FileObserver;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.interactors.GetFSOList;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.interactors.OnFileObserverEvent;
import corp.wmsoft.android.lib.filemanager.interactors.UpdateListSummary;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.PreferencesHelper;
import corp.wmsoft.android.lib.mvpcrx.presenter.MVPCPresenter;

import rx.Subscriber;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 3:49 PM.<br/>
 */
@SuppressLint("LongLogTag")
class FileManagerViewPresenter extends MVPCPresenter<IFileManagerViewContract.View> implements IFileManagerViewContract.Presenter {

    /**/
    private final static String TAG = "wmfm::FileManagerViewP";

    /**
     * Use cases
     */
    private final GetFSOList mGetFSOList;
    private final GetMountPoints mGetMountPoints;
    private final UpdateListSummary mUpdateListSummary;
    private final OnFileObserverEvent mOnFileObserverEvent;
    /**/
    private static final int FILE_OBSERVER_MASK =
                    FileObserver.CREATE | // add
                    FileObserver.DELETE | // remove
                    FileObserver.DELETE_SELF | // remove and go up
                    FileObserver.MOVED_FROM | // remove
                    FileObserver.MOVED_TO | // add
                    FileObserver.MODIFY | // update
                    FileObserver.MOVE_SELF; // remove and go up
    /**/
    private FileObserver mFileObserver;
    /**/
    private final FileManagerViewModel mViewModel;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentMode = IFileManagerNavigationMode.UNDEFINED;
    /**/
    private String mCurrentDir;
    /**/
    private List<BreadCrumb> breadCrumbsList;
    /**/
    private ObservableList.OnListChangedCallback<ObservableList<FSOViewModel>> mViewModelListener =
            new ObservableList.OnListChangedCallback<ObservableList<FSOViewModel>>() {
                @Override
                public void onChanged(ObservableList<FSOViewModel> fsoViewModels) {
                    mViewModel.isEmptyFolder.set(fsoViewModels.size() == 1 && fsoViewModels.get(0).fso.isParentDirectory());
                }

                @Override
                public void onItemRangeChanged(ObservableList<FSOViewModel> fsoViewModels,
                                               int positionStart, int itemCount) {}

                @Override
                public void onItemRangeInserted(ObservableList<FSOViewModel> fsoViewModels,
                                                int positionStart, int itemCount) {
                    mViewModel.isEmptyFolder.set(fsoViewModels.size() == 1 && fsoViewModels.get(0).fso.isParentDirectory());
                }

                @Override
                public void onItemRangeMoved(ObservableList<FSOViewModel> fsoViewModels,
                                             int fromPosition, int toPosition, int itemCount) {}

                @Override
                public void onItemRangeRemoved(ObservableList<FSOViewModel> fsoViewModels,
                                               int positionStart, int itemCount) {
                    mViewModel.isEmptyFolder.set(fsoViewModels.size() == 1 && fsoViewModels.get(0).fso.isParentDirectory());
                }
            };


    FileManagerViewPresenter(
            GetFSOList getFSOList,
            GetMountPoints getMountPoints,
            UpdateListSummary updateListSummary,
            OnFileObserverEvent onFileObserverEvent
    ) {

        this.mGetFSOList        = getFSOList;
        this.mGetMountPoints    = getMountPoints;
        this.mUpdateListSummary = updateListSummary;
        this.mOnFileObserverEvent = onFileObserverEvent;

        //Initialize variables
        breadCrumbsList = new ArrayList<>();
        mViewModel = new FileManagerViewModel();
        mViewModel.fsoViewModels.addOnListChangedCallback(mViewModelListener);
    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        super.attachView(mvpView);
        getView().setViewModel(mViewModel);
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        releaseFileObserver();
        mViewModel.fsoViewModels.removeOnListChangedCallback(mViewModelListener);
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
        if (mCurrentMode == IFileManagerNavigationMode.UNDEFINED)
            setViewMode(PreferencesHelper.getFileManagerNavigationMode()); // Set the default configuration if this is first launch
        else
            setViewMode(mCurrentMode);

        if (mCurrentDir == null) {
            // get mount point manually or ask for it from MountPointsView if it set
            loadMountPoints();
        } else {
            if (mViewModel.fsoViewModels.isEmpty() && !mViewModel.isLoading.get())
                changeCurrentDir(mCurrentDir, true);
        }
    }

    /**
     * Called from data binding
     * @param fsoViewModel view model
     */
    @Override
    public void onFSOPicked(FSOViewModel fsoViewModel) {
        if (fsoViewModel.fso.isParentDirectory()) {
            onGoBack();
        } else if (fsoViewModel.fso.isDirectory()) {
            changeCurrentDir(fsoViewModel.fso.getFullPath(), true);
        } else {
            if (isViewAttached())
                getView().filePicked(fsoViewModel.fso.getFullPath());
        }
    }

    @Override
    public void openMountPoint(MountPoint mountPoint) {
        breadCrumbsList.clear();
        changeCurrentDir(mountPoint.getPath(), true);
    }

    @Override
    public void onSetTimeFormat(@IFileManagerFileTimeFormat int format) {

        PreferencesHelper.setFileManagerFileTimeFormat(format);

        if (mCurrentMode == IFileManagerNavigationMode.ICONS ||
            mCurrentMode == IFileManagerNavigationMode.SIMPLE)
            return;

        UpdateListSummary.RequestValues requestValues = new UpdateListSummary.RequestValues(mViewModel);
        executeUseCase(mUpdateListSummary, requestValues, new Subscriber<Boolean>() {
            @Override
            public void onStart() {
                FileHelper.sReloadDateTimeFormats = true;
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError("+e+")");
                // TODO - show error
            }

            @Override
            public void onNext(Boolean isFinished) {}
        });
    }

    @Override
    public boolean isShowHidden() {
        return PreferencesHelper.isShowHidden();
    }

    @Override
    public void setShowHidden(boolean isVisible) {
        PreferencesHelper.setShowHidden(isVisible);
        // TODO - не делать перезагрузку, а скрывать и показывать
        changeCurrentDir(mCurrentDir, false);
    }

    @Override
    public boolean isShowDirsFirst() {
        return PreferencesHelper.isShowDirsFirst();
    }

    @Override
    public void setShowDirsFirst(boolean isDirsFirst) {
        PreferencesHelper.setShowDirsFirst(isDirsFirst);
        // TODO - не делать перезагрузку, а просто сортировать
        changeCurrentDir(mCurrentDir, false);
    }

    @Override
    public void setSortMode(@IFileManagerSortMode int mode) {
        PreferencesHelper.setFileManagerSortMode(mode);
        // TODO - не делать перезагрузку, а просто сортировать
        changeCurrentDir(mCurrentDir, false);
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
    }

    @Override
    public String getCurrentDir() {
        return mCurrentDir;
    }

    @Override
    public boolean onGoBack() {
        if (breadCrumbsList.size() > 1) {
            breadCrumbsList.remove(breadCrumbsList.size() - 1);
            changeCurrentDir(breadCrumbsList.get(breadCrumbsList.size() - 1).getCurrentDirectory(), false);
            return true;
        }
        return false;
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
    private void changeCurrentDir(final String newDir, final boolean addToBreadCrumb) {
        this.mCurrentDir = newDir;
        loadFSOList(addToBreadCrumb);
    }

    private void loadMountPoints() {

        executeUseCase(mGetMountPoints, new GetMountPoints.RequestValues(true), new Subscriber<List<MountPoint>>() {

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "loadMountPoints.onError("+e+")");
                // TODO - show error
            }

            @Override
            public void onNext(List<MountPoint> mountPoints) {
                // get first available mount point
                MountPoint mountPoint = mountPoints.get(0);
                openMountPoint(mountPoint);
            }
        });
    }

    private void loadFSOList(final boolean addToBreadCrumb) {

        executeUseCase(mGetFSOList, new GetFSOList.RequestValues(mCurrentDir), new Subscriber<List<FSOViewModel>>() {
            @Override
            public void onStart() {
                mViewModel.fsoViewModels.clear();
                mViewModel.isLoading.set(true);
            }

            @Override
            public void onCompleted() {
                setupFileObserver(addToBreadCrumb);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "loadFSOList.onError("+e+")");
                // TODO - show error
            }

            @Override
            public void onNext(List<FSOViewModel> fsoViewModelList) {
                mViewModel.fsoViewModels.addAll(fsoViewModelList);
            }
        });
    }

    private void setupFileObserver(final boolean addToBreadCrumb) {

        releaseFileObserver();

        mFileObserver = new FileObserver(mCurrentDir, FILE_OBSERVER_MASK) {
            @Override
            public void onEvent(int event, String path) {

                OnFileObserverEvent.RequestValues requestValues = new OnFileObserverEvent.RequestValues(mViewModel, event, path, mCurrentDir);

                executeUseCase(mOnFileObserverEvent, requestValues, new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "setupFileObserver.onError("+e+")");
                        // TODO - show error
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {}
                });

            }
        };

        mFileObserver.startWatching();

        // send event to view
        if (isViewAttached())
            getView().directoryChanged(mCurrentDir);

        if (addToBreadCrumb)
            breadCrumbsList.add(new BreadCrumb(mCurrentDir));

        mViewModel.isLoading.set(false);
    }

    private void releaseFileObserver() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }

}
