package corp.wmsoft.android.lib.filemanager.interactors;

import android.content.Context;
import android.os.storage.StorageVolume;
import android.support.annotation.NonNull;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.mapper.Mapper;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.rx.RxStorageHelper;
import corp.wmsoft.android.lib.mvpc.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpc.util.IMVPCSchedulerProvider;
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
                .toList();
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
