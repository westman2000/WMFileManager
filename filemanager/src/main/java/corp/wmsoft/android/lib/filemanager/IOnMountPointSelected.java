package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.models.MountPoint;


/**
 * <p>Created by WestMan2000 on 9/26/16. <p>
 */
@Keep
@Deprecated
public interface IOnMountPointSelected {

    void onMountPointSelected(MountPoint mountPoint);

}
