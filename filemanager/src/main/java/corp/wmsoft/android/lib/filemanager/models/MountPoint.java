package corp.wmsoft.android.lib.filemanager.models;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Environment;

import com.google.auto.value.AutoValue;

import java.util.Locale;
import java.util.Random;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.util.IconsHelper;
import corp.wmsoft.android.lib.mvpcrx.viewmodel.IMVPCViewModel;


/**
 * <br/>Created by WestMan2000 on 9/2/16 at 2:04 PM.<br/>
 */
@AutoValue
public abstract class MountPoint implements IMVPCViewModel {

    //Resource identifier for icon
    private static final int RESOURCE_ICON_SD_CARD_SELECTED    = R.drawable.ic_sd_storage_selected_24dp;
    private static final int RESOURCE_ICON_SD_CARD_UNSELECTED  = R.drawable.ic_sd_storage_unselected_24dp;
    private static final int RESOURCE_ICON_INTERNAL_SELECTED   = R.drawable.ic_storage_selected_24dp;
    private static final int RESOURCE_ICON_INTERNAL_UNSELECTED = R.drawable.ic_storage_unselected_24dp;
    private static final int RESOURCE_ICON_USB_SELECTED        = R.drawable.ic_usb_selected_24dp;
    private static final int RESOURCE_ICON_USB_UNSELECTED      = R.drawable.ic_usb_unselected_24dp;

    /**/
    public abstract int      id();
    /**/
    public abstract String   fullPath();
    /**/
    public abstract String   description();
    /**/
    public abstract StateListDrawable icon();
    /**/
    public abstract boolean  isRemovable();
    /**/
    public abstract boolean  isPrimary();
    /**/
    public abstract String   state();
    /**
     * Is mount point mounted
     * @return true if mounted, false otherwise
     *
     * @see Environment#MEDIA_MOUNTED
     * @see Environment#MEDIA_UNMOUNTED
     */
    public boolean isMounted() {
        return state().equals(Environment.MEDIA_MOUNTED);
    }

    private static MountPoint.Builder builder() {
        return new AutoValue_MountPoint.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {

        abstract Builder setId(int id);
        abstract Builder setFullPath(String fullPath);
        abstract Builder setDescription(String fullPath);
        abstract Builder setIcon(StateListDrawable icon);
        abstract Builder setIsRemovable(boolean isRemovable);
        abstract Builder setIsPrimary(boolean isPrimary);
        abstract Builder setState(String state);

        abstract MountPoint build();
    }

    public static MountPoint create(String path, String description, boolean isRemovable, boolean isPrimary, String state) {
        Random random = new Random();
        return MountPoint.builder()
                .setId(random.nextInt())
                .setFullPath(path)
                .setDescription(description)
                .setIsRemovable(isRemovable)
                .setIsPrimary(isPrimary)
                .setState(state)
                .setIcon(getIconStateListDrawable(path, isRemovable))
                .build();
    }

    public static MountPoint create(String path, String state,  boolean isRemovable) {
        Random random = new Random();
        return MountPoint.builder()
                .setId(random.nextInt())
                .setFullPath(path)
                .setDescription(WMFileManager.getApplicationContext().getString(R.string.wm_fm_external_storage))
                .setIsRemovable(isRemovable)
                .setIsPrimary(true)
                .setState(state)
                .setIcon(getIconStateListDrawable(path, false))
                .build();
    }

    private static StateListDrawable getIconStateListDrawable(String path, boolean isRemovable) {

        int selectedIconResId = isRemovable ? RESOURCE_ICON_SD_CARD_SELECTED : RESOURCE_ICON_INTERNAL_SELECTED;
        int unselectedIconResId = isRemovable ? RESOURCE_ICON_SD_CARD_UNSELECTED : RESOURCE_ICON_INTERNAL_UNSELECTED;

        if (path.toLowerCase(Locale.ROOT).contains("usb")) {
            selectedIconResId = RESOURCE_ICON_USB_SELECTED;
            unselectedIconResId = RESOURCE_ICON_USB_UNSELECTED;
        }

        Drawable selectedDrawable = IconsHelper.getDrawable(selectedIconResId);
        Drawable unselectedDrawable = IconsHelper.getDrawable(unselectedIconResId);

        StateListDrawable stateList = new StateListDrawable();
        stateList.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
        stateList.addState(new int[]{-android.R.attr.state_selected}, unselectedDrawable);
        stateList.addState(new int[]{}, unselectedDrawable);

        return stateList;
    }

}
