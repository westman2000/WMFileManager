package corp.wmsoft.android.lib.filemanager.util.rx;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import corp.wmsoft.android.lib.filemanager.R;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by WestMan2000 on 8/28/16. <br/>
 * A helper class with useful methods for deal with storage's.
 */
public class RxStorageHelper {

    /**/
    private static List<StorageVolume> sStorageVolumes;


    /**
     * Method that returns the storage volumes defined in the system.  This method uses
     * reflection to retrieve the method if API < 24
     *
     * @param context The current context to get application context
     * @return Observable<StorageVolume> The storage volume defined in the system
     */
    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public static Observable<StorageVolume> getStorageVolumes(Context context, boolean forceReload) {

        if (sStorageVolumes == null || forceReload) {

            final Context appContext = context.getApplicationContext();
            final StorageManager storageManager = (StorageManager) appContext.getSystemService(Context.STORAGE_SERVICE);

            sStorageVolumes = new ArrayList<>();

            return Observable.create(new Observable.OnSubscribe<StorageVolume>() {

                @Override
                public void call(Subscriber<? super StorageVolume> subscriber) {
                    if (!subscriber.isUnsubscribed()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            List<StorageVolume> storageVolumes = storageManager.getStorageVolumes();
                            for (StorageVolume storageVolume : storageVolumes) {
                                subscriber.onNext(storageVolume);
                                sStorageVolumes.add(storageVolume);
                            }
                        } else {
                            //Use reflect to get this value (if possible)
                            try {
                                Method method = storageManager.getClass().getMethod("getVolumeList");
                                StorageVolume[] storageVolumes = (StorageVolume[]) method.invoke(storageManager);
                                for (StorageVolume storageVolume : storageVolumes) {
                                    subscriber.onNext(storageVolume);
                                    sStorageVolumes.add(storageVolume);
                                }
                            } catch (Exception e) {
                                //Ignore. Android SDK StorageManager class doesn't have this method
                                //Use default android information from environment
                                try {
                                    File externalStorage = Environment.getExternalStorageDirectory();
                                    String path = externalStorage.getCanonicalPath();
                                    String description;
                                    //noinspection IndexOfReplaceableByContains
                                    if (path.toLowerCase(Locale.ROOT).indexOf("usb") != -1) {
                                        description = appContext.getString(R.string.usb_storage);
                                    } else {
                                        description = appContext.getString(R.string.external_storage);
                                    }
                                    // Create the object by reflection
                                    Constructor<StorageVolume> constructor =
                                            StorageVolume.class.
                                                    getConstructor(
                                                            String.class,   // String path
                                                            String.class,   // String description
                                                            boolean.class,  // boolean removable
                                                            boolean.class,  // boolean emulated
                                                            int.class,      // int mtpReserveSpace
                                                            boolean.class,  // boolean allowMassStorage
                                                            long.class);    // long maxFileSize
                                    StorageVolume sv = constructor.newInstance(path, description, false, false, 0, false, 0);
                                    subscriber.onNext(sv);
                                    sStorageVolumes.add(sv);
                                } catch (Exception ex2) {
                                    subscriber.onError(new Error("Can't get StorageVolumes from device"));
                                }
                            }
                        }
                        subscriber.onCompleted();
                    }
                }
            });

        }

        return Observable.from(sStorageVolumes);
    }

    /**
     * Method that returns the storage volume path.
     *
     * @param storageVolume The storage volume
     * @return String The path of the storage volume
     */
    @Nullable
    public static String getStorageVolumePath(StorageVolume storageVolume) {
        String mountPath = null;
        try {
            Method methodGetPath = storageVolume.getClass().getMethod("getPath");
            mountPath = (String) methodGetPath.invoke(storageVolume);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mountPath;
    }

    /**
     * Method that returns the storage volume description. This method uses
     * reflection to retrieve the description because CM10 has a {@link Context}
     * as first parameter, that AOSP hasn't.
     *
     * @param ctx The current context
     * @param storageVolume The storage volume
     * @return String The description of the storage volume, or path
     */
    @Nullable
    public static String getStorageVolumeDescription(Context ctx, StorageVolume storageVolume) {

        Context context = ctx.getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return storageVolume.getDescription(context);
        } else {
            try {
                Method methodGetDescription = storageVolume.getClass().getMethod("getDescription", Context.class);
                if (methodGetDescription == null) {
                    // AOSP
                    methodGetDescription = storageVolume.getClass().getMethod("getDescription");
                    return (String)methodGetDescription.invoke(storageVolume);
                }

                // CM10 or Android 7.0
                return (String)methodGetDescription.invoke(storageVolume, context);

            } catch (Exception e) {
                e.printStackTrace();
                // Returns the volume storage path
                return getStorageVolumePath(storageVolume);
            }
        }
    }

    /**
     * Returns true if the volume is removable.
     *
     * @return is removable
     */
    public static boolean isStorageVolumeRemovable(StorageVolume storageVolume) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return storageVolume.isRemovable();
        } else {
            try {
                Method methodIsRemovable = storageVolume.getClass().getMethod("isRemovable");
                return (boolean) methodIsRemovable.invoke(storageVolume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isStorageVolumePrimary(StorageVolume storageVolume) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return storageVolume.isPrimary();
        } else {
            try {
                Method methodIsPrimary = storageVolume.getClass().getMethod("isPrimary");
                return (boolean) methodIsPrimary.invoke(storageVolume);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getStorageVolumeState(Context context, StorageVolume storageVolume) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return storageVolume.getState();
        } else {
            try {
                final Context appContext = context.getApplicationContext();
                final StorageManager storageManager = (StorageManager) appContext.getSystemService(Context.STORAGE_SERVICE);
                Method methodGetVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
                return (String)methodGetVolumeState.invoke(storageManager, getStorageVolumePath(storageVolume));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Environment.MEDIA_UNMOUNTED;
    }

    /**
     * Method that returns if the path is a storage volume
     *
     * @param path The path
     * @return boolean If the path is a storage volume
     */
    public static boolean isStorageVolume(String path) {

        if (sStorageVolumes == null)
            return true;

        for (StorageVolume sv : sStorageVolumes) {
            String p = new File(path).getAbsolutePath();
            String volPath = getStorageVolumePath(sv);
            if (volPath == null) return true;
            String v = new File(volPath).getAbsolutePath();
            if (p.compareTo(v) == 0) {
                return true;
            }
        }
        return false;
    }
}
