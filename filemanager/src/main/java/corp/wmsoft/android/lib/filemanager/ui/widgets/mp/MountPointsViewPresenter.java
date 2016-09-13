package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.interactors.GetMountPoints;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpc.presenter.MVPCPresenter;
import rx.Subscriber;


/**
 * Created by WestMan2000 on 9/11/16. <br/>
 */
@SuppressLint("LongLogTag")
public class MountPointsViewPresenter extends MVPCPresenter<IMountPointsViewContract.View> implements IMountPointsViewContract.Presenter {

    /**/
    private final static String TAG = "MountPointsViewPresenter";

    /**
     * Use cases
     */
    private final GetMountPoints mGetMountPoints;
    /**/
    private List<MountPoint> mMountPoints;
    /**/
    private int mSelectedMountPointId = 0;


    public MountPointsViewPresenter(
            GetMountPoints getMountPoints) {

        Log.d(TAG, "MountPointsViewPresenter()");

        this.mGetMountPoints = getMountPoints;
    }

    @Override
    public void attachView(IMountPointsViewContract.View mvpView) {
        super.attachView(mvpView);

        Log.d(TAG, "attachView()");

        getView().showLoading();
        getView().sendEvent(IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION);
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
        selectMountPoint();
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
                    mSelectedMountPointId = mp.getId();
                    break;
                }
            }
        }

        selectMountPoint();
    }

    private void selectMountPoint() {
        for (MountPoint mp : mMountPoints) {
            if (mp.getId() == mSelectedMountPointId) {
                getView().selectMountPoint(mp);
                return;
            }
        }
    }
}
