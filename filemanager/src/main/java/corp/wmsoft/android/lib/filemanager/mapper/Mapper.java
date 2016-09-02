package corp.wmsoft.android.lib.filemanager.mapper;

import android.os.storage.StorageVolume;

import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.rx.RxStorageHelper;
import rx.Observable;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 2:10 PM.<br/>
 */
public class Mapper {


    public static Observable<MountPoint> map(final StorageVolume storageVolume) {

        return Observable.fromCallable(new Callable<MountPoint>() {
            @Override
            public MountPoint call() throws Exception {
                return new MountPoint(
                        RxStorageHelper.getStorageVolumePath(storageVolume),
                        RxStorageHelper.getStorageVolumeDescription(WMFileManager.getApplicationContext(), storageVolume),
                        RxStorageHelper.isStorageVolumeRemovable(storageVolume),
                        RxStorageHelper.isStorageVolumePrimary(storageVolume),
                        RxStorageHelper.getStorageVolumeState(WMFileManager.getApplicationContext(), storageVolume)
                );
            }
        });
    }

}
