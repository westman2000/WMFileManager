package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.FileObserver;
import android.util.Log;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
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
    private BroadcastReceiver mUnMountReceiver = null;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentMode = IFileManagerNavigationMode.UNDEFINED;
    /**/
    private String mCurrentDir;


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
        mViewModel = new FileManagerViewModel();

        registerExternalStorageListener();
    }

    @Override
    public void attachView(IFileManagerViewContract.View mvpView) {
        super.attachView(mvpView);
        getView().setViewModel(mViewModel);
        getView().askForExternalStoragePermission();
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        releaseFileObserver();
        unRegisterExternalStorageListener();
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

            if (mViewModel.selectedMountPoint != null)
                getView().selectMountPoint(mViewModel.selectedMountPoint);
        }
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {

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
            changeCurrentDir(fsoViewModel.fso.fullPath(), true);
        } else {
            if (isViewAttached())
                getView().filePicked(fsoViewModel.fso.fullPath());
        }
    }

    @Override
    public void onMountPointSelect(MountPoint mountPoint) {

        // do not select i already selected
        if (mountPoint == mViewModel.selectedMountPoint)
            return;

        selectMountPoint(mountPoint, false);
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
        if (mViewModel.breadCrumbs.size() > 1) {
            mViewModel.breadCrumbs.remove(mViewModel.breadCrumbs.size() - 1);
            changeCurrentDir(mViewModel.breadCrumbs.get(mViewModel.breadCrumbs.size() - 1).fullPath(), false);
            return true;
        }
        return false;
    }

    @Override
    public void onBreadCrumbClick(BreadCrumb breadCrumb) {

        // do not reload for same
        if (breadCrumb.fullPath().equals(mViewModel.breadCrumbs.get(mViewModel.breadCrumbs.size() - 1).fullPath()))
            return;

        for (int i = 0; i < mViewModel.breadCrumbs.size(); i++) {
            BreadCrumb bc = mViewModel.breadCrumbs.get(i);
            if (breadCrumb.fullPath().equals(bc.fullPath())) {
                mViewModel.breadCrumbs.subList(i+1, mViewModel.breadCrumbs.size()).clear();
            }
        }

        changeCurrentDir(mViewModel.breadCrumbs.get(mViewModel.breadCrumbs.size() - 1).fullPath(), false);
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

                mViewModel.mountPoints.addAll(mountPoints);

                // get first available mount point
                selectMountPoint(mountPoints.get(0), true);
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
            mViewModel.breadCrumbs.add(BreadCrumb.create(mCurrentDir));

        mViewModel.isLoading.set(false);
    }

    private void releaseFileObserver() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
            mFileObserver = null;
        }
    }

    /**
     * Select mount point and show respectively directory list of files
     * @param mountPoint mount point
     * @param isSendSelectEventToView is send event to view, so view show selected state for mount point
     */
    private void selectMountPoint(MountPoint mountPoint, boolean isSendSelectEventToView) {

        mViewModel.selectedMountPoint = mountPoint;

        mViewModel.breadCrumbs.clear();
        changeCurrentDir(mountPoint.fullPath(), true);

        if (isSendSelectEventToView)
            getView().selectMountPoint(mountPoint);
    }

    /**
     * Registers an intent to listen for ACTION_MEDIA_EJECT notifications.
     * The intent will call closeExternalStorageFiles() if the external media
     * is going to be ejected, so applications can clean up any files they have open.
     */
    private void registerExternalStorageListener() {
        if (mUnMountReceiver == null) {
            mUnMountReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(Intent.ACTION_MEDIA_EJECT)) {

                        // remove MountPoint
                        removeMountPointByPath(intent.getData().getPath());

                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {

                        // add mount point
                        addMountPointByPath(intent.getData().getPath());
                    }
                }
            };
            IntentFilter iFilter = new IntentFilter();
            iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
            iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
            iFilter.addDataScheme("file");
            // TODO - to read - https://developer.android.com/guide/topics/connectivity/usb/host.html
            WMFileManager.getApplicationContext().registerReceiver(mUnMountReceiver, iFilter);
        }
    }

    private void unRegisterExternalStorageListener() {
        if (mUnMountReceiver != null) {
            WMFileManager.getApplicationContext().unregisterReceiver(mUnMountReceiver);
            mUnMountReceiver = null;
        }
    }

    private void removeMountPointByPath(String path) {
        for (MountPoint mountPoint : mViewModel.mountPoints) {
            if (mountPoint.fullPath().contains(path)) {
                mViewModel.mountPoints.remove(mountPoint);
                return;
            }
        }
    }

    private void addMountPointByPath(final String path) {
        executeUseCase(mGetMountPoints, new GetMountPoints.RequestValues(true), new Subscriber<List<MountPoint>>() {

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "addMountPointByPath.onError("+e+")");
                // TODO - show error
            }

            @Override
            public void onNext(List<MountPoint> mountPoints) {
                for (MountPoint mp : mountPoints) {
                    if (mp.fullPath().equals(path)) {
                        mViewModel.mountPoints.add(mp);
                        return;
                    }
                }
            }
        });
    }
}
