package corp.wmsoft.android.lib.filemanager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.FrameLayout;

import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerViewInternal;


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

    public void showFilesInPath(String path) {
        this.mFileManagerViewInternal.showFilesInPath(path);
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
    public void setTimeFormat(@IFileManagerFileTimeFormat int format) {
        mFileManagerViewInternal.setTimeFormat(format);
    }

    /**
     *
     */
    @IFileManagerFileTimeFormat
    public int getFileTimeFormat() {
        return mFileManagerViewInternal.getFileTimeFormat();
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
    @IFileManagerNavigationMode
    public int getNavigationMode() {
        return mFileManagerViewInternal.getNavigationMode();
    }

    /**
     *
     */
    public boolean isShowHidden() {
        return mFileManagerViewInternal.isShowHidden();
    }

    /**
     *
     */
    public boolean isShowDirsFirst() {
        return mFileManagerViewInternal.isShowDirsFirst();
    }

    /**
     *
     */
    public void setShowDirsFirst(boolean isDirsFirst) {
        mFileManagerViewInternal.setShowDirsFirst(isDirsFirst);
    }

    /**
     *
     */
    public void setShowHidden(boolean isVisible) {
        mFileManagerViewInternal.setShowHidden(isVisible);
    }

    /**
     *
     */
    public boolean isShowThumbs() {
        return mFileManagerViewInternal.isShowThumbs();
    }

    /**
     *
     */
    public void setShowThumbs(boolean isShowThumbs) {
        mFileManagerViewInternal.setShowThumbs(isShowThumbs);
    }

    /**
     *
     */
    public void setSortMode(@IFileManagerSortMode int mode) {
        mFileManagerViewInternal.setSortMode(mode);
    }

    /**
     *
     */
    @IFileManagerSortMode
    public int getSortMode() {
        return mFileManagerViewInternal.getSortMode();
    }

    /**
     *
     */
    public String getCurrentDirectory() {
        return mFileManagerViewInternal.getCurrentDirectory();
    }

    /**
     *
     */
    public void setRestrictions(SparseArray restrictions) {
        WMFileManager.setRestrictions(restrictions);
    }

    /**
     * Method that sets the listener for picked items
     *
     * @param onFilePickedListener The listener reference
     */
    public void setOnFilePickedListener(IOnFilePickedListener onFilePickedListener) {
        mFileManagerViewInternal.setOnFilePickedListener(onFilePickedListener);
    }

    /**
     * Method that sets the listener for directory changes
     *
     * @param onDirectoryChangedListener The listener reference
     */
    public void setOnDirectoryChangedListener(IOnDirectoryChangedListener onDirectoryChangedListener) {
        mFileManagerViewInternal.setOnDirectoryChangedListener(onDirectoryChangedListener);
    }

    private void init(Context context) {
        this.mFileManagerViewInternal = new FileManagerViewInternal(context);

        addView(mFileManagerViewInternal);

    }
}