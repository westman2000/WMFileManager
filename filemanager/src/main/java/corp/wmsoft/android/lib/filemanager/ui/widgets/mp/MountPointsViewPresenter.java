package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.mvpcrx.presenter.MVPCPresenter;
import rx.Subscriber;


/**
 * Created by WestMan2000 on 9/11/16. <br/>
 */
@Deprecated
class MountPointsViewPresenter extends MVPCPresenter<IMountPointsViewContract.View> implements IMountPointsViewContract.Presenter {

    /**/
    private final static String TAG = "wmfm::MountPointsViewP";

    /**
     * Use cases
     */
    private final GetMountPoints mGetMountPoints;
    /**/
    private List<MountPoint> mMountPoints;
    /**/
    private int mSelectedMountPointId = 0;
    /**/
    private BroadcastReceiver mUnMountReceiver = null;


    MountPointsViewPresenter(GetMountPoints getMountPoints) {
        this.mGetMountPoints = getMountPoints;
        registerExternalStorageListener();
    }

    @Override
    public void attachView(IMountPointsViewContract.View mvpView) {
        super.attachView(mvpView);
        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
    }

    @Override
    public void onDestroyed() {
        super.onDestroyed();
        unRegisterExternalStorageListener();
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        if (mMountPoints == null)
            loadMountPoints();
        else
            showMountPoints();
    }

    @Override
    public void onSelectMountPoint(int mountPointId) {
        mSelectedMountPointId = mountPointId;
        selectMountPoint(true);
    }

    private void loadMountPoints() {

        executeUseCase(mGetMountPoints, new GetMountPoints.RequestValues(true), new Subscriber<List<MountPoint>>() {
            @Override
            public void onCompleted() {
                showMountPoints();
            }

            @Override
            public void onError(Throwable e) {
                getView().showError(new Error(e));
            }

            @Override
            public void onNext(List<MountPoint> mountPoints) {
                mMountPoints = new ArrayList<>(mountPoints);
            }
        });
    }

    private void showMountPoints() {
        getView().setData(mMountPoints);
        getView().showContent();
        getView().hideLoading();

        if (mSelectedMountPointId == 0) {
            // get Primary mount point and select it
            for (MountPoint mp : mMountPoints) {
                if (mp.isPrimary()) {
                    mSelectedMountPointId = mp.id();
                    break;
                }
            }
        }

        selectMountPoint(false);
    }

    private void selectMountPoint(boolean fireEvent) {
        for (MountPoint mp : mMountPoints) {
            if (mp.id() == mSelectedMountPointId) {
                getView().selectMountPoint(mp, fireEvent);
                return;
            }
        }
    }

    private void onStorageVolumeMounted(final String mountedPath) {
        loadMountPoints();
    }

    private void onStorageVolumeUnMounted(final String unMountedPath) {
        loadMountPoints();
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
                        onStorageVolumeUnMounted(intent.getData().getPath());
                    } else if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                        onStorageVolumeMounted(intent.getData().getPath());
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
}
