package corp.wmsoft.android.lib.filemanager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Keep;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import corp.wmsoft.android.lib.filemanager.ui.widgets.mp.MountPointViewInternal;


/**
 * <p>Created by WestMan2000 on 9/26/16. <p>
 */
@Keep
public class MountPointView extends LinearLayout {

    /**/
    private MountPointViewInternal mMountPointViewInternal;


    public MountPointView(Context context) {
        super(context);
        this.mMountPointViewInternal = new MountPointViewInternal(context);
        addView(mMountPointViewInternal);
    }

    public MountPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMountPointViewInternal = new MountPointViewInternal(context, attrs);
        addView(mMountPointViewInternal);
    }

    public MountPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mMountPointViewInternal = new MountPointViewInternal(context, attrs, defStyleAttr);
        addView(mMountPointViewInternal);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MountPointView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mMountPointViewInternal = new MountPointViewInternal(context, attrs, defStyleAttr, defStyleRes);
        addView(mMountPointViewInternal);
    }

    /**
     *
     */
    public void onExternalStoragePermissionsGranted() {
        this.mMountPointViewInternal.onExternalStoragePermissionsGranted();
    }

    /**
     *
     */
    public void onExternalStoragePermissionsNotGranted() {
        this.mMountPointViewInternal.onExternalStoragePermissionsNotGranted();
    }

    /**
     *
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        mMountPointViewInternal.setOnFileManagerEventListener(onFileManagerEventListener);
    }

    /**
     *
     */
    public void setOnMountPointSelected(IOnMountPointSelected onMountPointSelectedListener) {
        mMountPointViewInternal.setOnMountPointSelected(onMountPointSelectedListener);
    }

}
