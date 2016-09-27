package corp.wmsoft.android.lib.filemanager.models;

import android.os.Environment;
import android.support.annotation.Keep;

import java.io.File;
import java.util.Locale;
import java.util.Random;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.mvpcrx.viewmodel.IMVPCViewModel;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 2:04 PM.<br/>
 */
public class MountPoint implements IMVPCViewModel {

    //Resource identifier for icon
    private static final int RESOURCE_ICON_SD_CARD  = R.drawable.ic_sd_storage_24dp;
    private static final int RESOURCE_ICON_INTERNAL = R.drawable.ic_storage_24dp;
    private static final int RESOURCE_ICON_USB      = R.drawable.ic_usb_24dp;

    /**/
    private int     mId;
    /**/
    private String  mPath;
    /**/
    private String  mDescription;
    /**/
    private int     mIconResId;
    /**/
    private boolean isRemovable;
    /**/
    private boolean isPrimary;
    /**/
    private String  mState;
    /**/
    private boolean isReadable;
    /**/
    private boolean isWritable;


    public MountPoint(String path, String description, boolean isRemovable, boolean isPrimary, String state) {
        Random random = new Random();
        this.mId          = random.nextInt();
        this.mPath        = path;
        this.mDescription = description;
        this.isRemovable  = isRemovable;
        this.isPrimary    = isPrimary;
        this.mState       = state;

        setupVariables();
        setupIconResId();
    }

    @Keep
    public int getId() {
        return mId;
    }

    @Keep
    public String getPath() {
        return mPath;
    }

    @Keep
    public String getDescription() {
        return mDescription;
    }

    @Keep
    public int getIconResId() {
        return mIconResId;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    @Keep
    public boolean isMounted() {
        return mState.equals(Environment.MEDIA_MOUNTED);
    }

    @Keep
    public String getState() {
        return mState;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public boolean isWritable() {
        return isWritable;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        MountPoint other = (MountPoint) obj;

        return other.getPath() != null && mPath.equals(other.getPath());

    }

    private void setupVariables() {

        if (mPath == null) return;

        File file = new File(mPath);
        this.isReadable = file.canRead();
        this.isWritable = file.canWrite();
    }

    private void setupIconResId() {

        mIconResId = isRemovable ? RESOURCE_ICON_SD_CARD : RESOURCE_ICON_INTERNAL;

        if (mPath == null) return;

        //noinspection IndexOfReplaceableByContains
        if (mPath.toLowerCase(Locale.ROOT).indexOf("usb") != -1) {
            mIconResId = RESOURCE_ICON_USB;
        }

    }

    @Override
    public String toString() {
        return "MountPoint{" +
                "mId=" + mId +
                ", mPath='" + mPath + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mIconResId=" + mIconResId +
                ", isRemovable=" + isRemovable +
                ", isPrimary=" + isPrimary +
                ", mState='" + mState + '\'' +
                ", isReadable=" + isReadable +
                ", isWritable=" + isWritable +
                '}';
    }
}
