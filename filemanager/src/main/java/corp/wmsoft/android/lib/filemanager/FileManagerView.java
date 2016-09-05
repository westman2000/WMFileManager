package corp.wmsoft.android.lib.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerViewInternal;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IOnFileManagerEventListener;


/**
 * Created by WestMan2000 on 9/4/16. <br/>
 */
public class FileManagerView extends FrameLayout {

    /**/
    private FileManagerViewInternal mFileManagerViewInternal;


    public FileManagerView(Context context) {
        super(context);
        init(context);
    }

    public FileManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FileManagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     *
     */
    public void onExternalStoragePermissionsGranted() {
        this.mFileManagerViewInternal.onExternalStoragePermissionsGranted();
    }

    /**
     *
     */
    public void onExternalStoragePermissionsNotGranted() {
        this.mFileManagerViewInternal.onExternalStoragePermissionsNotGranted();
    }

    /**
     * Method that sets the listener for events
     *
     * @param onFileManagerEventListener The listener reference
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        this.mFileManagerViewInternal.setOnFileManagerEventListener(onFileManagerEventListener);
    }

    /**
     *
     */
    public void setNavigationMode(@IFileManagerNavigationMode int mode) {
        mFileManagerViewInternal.setNavigationMode(mode);
    }

    /**
     *
     */
    public int getNavigationMode() {
        return mFileManagerViewInternal.getNavigationMode();
    }

    private void init(Context context) {
        this.mFileManagerViewInternal = new FileManagerViewInternal(context);

        addView(mFileManagerViewInternal);

    }
}