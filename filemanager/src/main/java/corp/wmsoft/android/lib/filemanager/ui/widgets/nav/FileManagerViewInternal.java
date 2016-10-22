package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.IOnDirectoryChangedListener;
import corp.wmsoft.android.lib.filemanager.IOnFileManagerEventListener;
import corp.wmsoft.android.lib.filemanager.IOnFilePickedListener;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.FSOViewModelAdapter;
import corp.wmsoft.android.lib.filemanager.databinding.FileManagerViewLayoutBinding;
import corp.wmsoft.android.lib.filemanager.mapper.FileSystemObjectMapper;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpcrx.predefined.MVPCFrameLayout;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 2:42 PM.<br/>
 *
 * @hide
 */
// TODO - add History
// TODO - after history - add back action
public class FileManagerViewInternal extends MVPCFrameLayout<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> implements IFileManagerViewContract.View {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private static final int ITEM_VIEW_CACHE_SIZE = 40;

    /**/
    private FileManagerViewLayoutBinding binding;

    /**/
    private LinearLayoutManager mLinearLayoutManager;
    /**/
    private GridLayoutManager mGridLayoutManager;
    /**/
    private DividerItemDecoration mDividerItemDecoration;
    /**/
    private FSOViewModelAdapter mAdapter;
    /**/
    private IOnFileManagerEventListener mOnFileManagerEventListener;
    /**/
    private IOnFilePickedListener mOnFilePickedListener;
    /**/
    private IOnDirectoryChangedListener mOnDirectoryChangedListener;



    public FileManagerViewInternal(Context context) {
        super(context);
        init();
    }

    public FileManagerViewInternal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FileManagerViewInternal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    public FileManagerViewInternal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * @hide
     */
    @Override
    protected IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> providePresenterFactory() {
        return new FileManagerViewPresenterFactory();
    }

    /**
     * @hide
     */
    @Override
    protected int provideUniqueIdentifier() {
        return 640344;
    }

    @Override
    public void onInitializePresenter(IFileManagerViewContract.Presenter presenter) {
        mAdapter.setPresenter(presenter);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mOnFileManagerEventListener == null)
            throw new RuntimeException("IOnFileManagerEventListener not set! Call FileManagerView.setOnFileManagerEventListener() to set listener.");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mOnFileManagerEventListener = null;
        mOnFilePickedListener = null;
        mOnDirectoryChangedListener = null;
    }

    @Override
    public void setViewModel(FileManagerViewModel viewModel) {
        binding.setViewModel(viewModel);
        mAdapter.setList(viewModel.fsoViewModels);
    }

    /**
     * @hide
     */
    @Override
    public void sendEvent(@IFileManagerEvent int event) {
        if (mOnFileManagerEventListener != null)
            mOnFileManagerEventListener.onFileManagerEvent(event);
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        getPresenter().onExternalStoragePermissionsGranted();
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {
    }

    /**
     * @hide
     */
    @Override
    public void showAsList() {
        // use a linear layout manager for simple and details mode
        if (mLinearLayoutManager == null)
            mLinearLayoutManager = new LinearLayoutManager(getContext());

        binding.fsoList.setLayoutManager(mLinearLayoutManager);

        if (mDividerItemDecoration == null)
            mDividerItemDecoration = new DividerItemDecoration(binding.fsoList.getContext(), LinearLayoutManager.VERTICAL);

        binding.fsoList.addItemDecoration(mDividerItemDecoration);
    }

    /**
     * @hide
     */
    @Override
    public void showAsGrid() {
        if (mGridLayoutManager == null)
            mGridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.wm_fm_default_grid_columns));

        binding.fsoList.setLayoutManager(mGridLayoutManager);

        binding.fsoList.removeItemDecoration(mDividerItemDecoration);
    }

    /**
     * @hide
     */
    @Override
    public void setNavigationModeInternal(@IFileManagerNavigationMode int mode) {
        mAdapter.setNavigationMode(mode);
    }

    @Override
    public void filePicked(String file) {
        if (mOnFilePickedListener != null)
            mOnFilePickedListener.onFilePicked(file);
    }

    @Override
    public void directoryChanged(String dir) {
        if (mOnDirectoryChangedListener != null)
            mOnDirectoryChangedListener.onDirectoryChanged(dir);
    }

    @Override
    public void showFilesInPath(String path) {
        getPresenter().showFilesInPath(path);
    }

    /**
     * @public
     */
    public void setTimeFormat(@IFileManagerFileTimeFormat int format) {
        getPresenter().onSetTimeFormat(format);
    }

    /**
     * @public
     */
    @IFileManagerFileTimeFormat
    public int getFileTimeFormat() {
        return getPresenter().getCurrentFileTimeFormat();
    }

    /**
     * @public
     */
    public void setNavigationMode(@IFileManagerNavigationMode int mode) {
        getPresenter().changeViewMode(mode);
    }

    /**
     * @public
     */
    @IFileManagerNavigationMode
    public int getNavigationMode() {
        return getPresenter().getCurrentMode();
    }

    /**
     * @public
     */
    public boolean isShowHidden() {
        return getPresenter().isShowHidden();
    }

    /**
     * @public
     */
    public void setShowHidden(boolean isVisible) {
        getPresenter().setShowHidden(isVisible);
    }

    /**
     * @public
     */
    public boolean isShowDirsFirst() {
        return getPresenter().isShowDirsFirst();
    }

    /**
     * @public
     */
    public void setShowDirsFirst(boolean isDirsFirst) {
        getPresenter().setShowDirsFirst(isDirsFirst);
    }

    /**
     * @public
     */
    public boolean isShowThumbs() {
        return getPresenter().isShowThumbs();
    }

    /**
     * @public
     */
    public void setShowThumbs(boolean isShowThumbs) {
        getPresenter().setShowThumbs(isShowThumbs);
        mAdapter.notifyDataPayloadChanged();
    }

    /**
     * @public
     */
    public void setSortMode(@IFileManagerSortMode int mode) {
        getPresenter().setSortMode(mode);
    }

    /**
     * @public
     */
    @IFileManagerSortMode
    public int getSortMode() {
        return getPresenter().getSortMode();
    }

    /**
     * @public
     */
    public String getCurrentDirectory() {
        return getPresenter().getCurrentDir();
    }

    /**
     * Method that sets the listener for events
     *
     * @param onFileManagerEventListener The listener reference
     *
     * @public
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        this.mOnFileManagerEventListener = onFileManagerEventListener;
    }

    /**
     * Method that sets the listener for picked items
     *
     * @param onFilePickedListener The listener reference
     *
     * @public
     */
    public void setOnFilePickedListener(IOnFilePickedListener onFilePickedListener) {
        this.mOnFilePickedListener = onFilePickedListener;
    }

    /**
     * Method that sets the listener for directory changes
     *
     * @param onDirectoryChangedListener The listener reference
     *
     * @public
     */
    public void setOnDirectoryChangedListener(IOnDirectoryChangedListener onDirectoryChangedListener) {
        this.mOnDirectoryChangedListener = onDirectoryChangedListener;
    }

    /**
     * Method that initializes the view. This method loads all the necessary
     * information and create an appropriate layout for the view.
     */
    private void init() {

        // Create data binding for file_manager_view_layout.xml
        binding = FileManagerViewLayoutBinding.inflate(LayoutInflater.from(getContext()));

        mAdapter = new FSOViewModelAdapter();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.fsoList.setHasFixedSize(true);
        // optimization for fast scroll, but maybe i will remove it if will be bug with selected state
        binding.fsoList.setItemViewCacheSize(ITEM_VIEW_CACHE_SIZE);
        binding.fsoList.setDrawingCacheEnabled(true);
        binding.fsoList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        binding.setAdapter(mAdapter);

        addView(binding.getRoot());
    }

}