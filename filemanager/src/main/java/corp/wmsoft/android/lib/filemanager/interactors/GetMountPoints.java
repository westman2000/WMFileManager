package corp.wmsoft.android.lib.filemanager.interactors;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageVolume;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.mapper.Mapper;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.rx.RxStorageHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;
import rx.Observable;
import rx.functions.Func1;


/**
 * Created by WestMan2000 on 8/29/16 at 2:23 PM.
 */
public class GetMountPoints extends MVPCUseCase<GetMountPoints.RequestValues, List<MountPoint>> {

    /**/
    private final Context mContext;


    public GetMountPoints(IMVPCSchedulerProvider schedulerProvider, Context context) {
        super(schedulerProvider);
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<List<MountPoint>> buildUseCaseObservable(@NonNull RequestValues requestValues) {
        //noinspection MissingPermission
        return RxStorageHelper.getStorageVolumes(mContext, requestValues.isForceReload())
                .flatMap(new Func1<StorageVolume, Observable<MountPoint>>() {
                    @Override
                    public Observable<MountPoint> call(StorageVolume storageVolume) {
                        return Mapper.map(storageVolume);
                    }
                })
                .filter(new Func1<MountPoint, Boolean>() {
                    @Override
                    public Boolean call(MountPoint mountPoint) {
                        return mountPoint.isMounted();
                    }
                })
                .toList()
                .map(new Func1<List<MountPoint>, List<MountPoint>>() {
                    @Override
                    public List<MountPoint> call(List<MountPoint> mountPoints) {
                        if (mountPoints.size() == 0) {
                            List<MountPoint> mountPointList = new ArrayList<>();
                            mountPointList.add(
                                    MountPoint.create(
                                            Environment.getExternalStorageDirectory().getPath(),
                                            Environment.getExternalStorageState(),
                                            Environment.isExternalStorageRemovable()
                                    )
                            );
                            return mountPointList;
                        }

                        return mountPoints;
                    }
                });
    }

    public static class RequestValues extends MVPCUseCase.RequestValues {

        /**/
        private final boolean isForceReload;


        public RequestValues() {
            this.isForceReload = false;
        }

        public RequestValues(boolean forceReload) {
            this.isForceReload = forceReload;
        }

        public boolean isForceReload() {
            return isForceReload;
        }
    }
}
