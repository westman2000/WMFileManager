package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.IOnDirectoryChangedListener;
import corp.wmsoft.android.lib.filemanager.IOnFileManagerEventListener;
import corp.wmsoft.android.lib.filemanager.IOnFilePickedListener;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.BreadCrumbAdapter;
import corp.wmsoft.android.lib.filemanager.adapters.FSOViewModelAdapter;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmFileManagerViewLayoutBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.AndroidHelper;
import corp.wmsoft.android.lib.mvpcrx.predefined.MVPCFrameLayout;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 2:42 PM.<br/>
 *
 * @hide
 */
public class FileManagerViewInternal extends MVPCFrameLayout<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> implements IFileManagerViewContract.View {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private static final int ITEM_VIEW_CACHE_SIZE = 40;

    /**/
    private WmFmFileManagerViewLayoutBinding binding;

    /**/
    private LinearLayoutManager mVerticalLinearLayoutManager;
    /**/
    private GridLayoutManager mGridLayoutManager;
    /**/
    private DividerItemDecoration mDividerItemDecoration;
    /**/
    private FSOViewModelAdapter fsoViewModelAdapter;
    /**/
    private BreadCrumbAdapter breadCrumbAdapter;
    /**/
    private IOnFileManagerEventListener mOnFileManagerEventListener;
    /**/
    private IOnFilePickedListener mOnFilePickedListener;
    /**/
    private IOnDirectoryChangedListener mOnDirectoryChangedListener;

    /**/
    private ObservableList<MountPoint> mountPoints;
    /**/
    private ObservableList.OnListChangedCallback<ObservableList<MountPoint>> mountPointsListCallback =
            new ObservableList.OnListChangedCallback<ObservableList<MountPoint>>() {

                @Override
                public void onChanged(ObservableList<MountPoint> mountPoints) {
//                    Log.d(TAG, "onChanged("+mountPoints+")");
                }

                @Override
                public void onItemRangeChanged(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {
//                    Log.d(TAG, "onItemRangeChanged("+mountPoints+", "+positionStart+", "+itemCount+")");
                }

                @Override
                public void onItemRangeInserted(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {
//                    Log.d(TAG, "onItemRangeInserted("+mountPoints+", "+positionStart+", "+itemCount+")");
                    addMountPointTabItems(mountPoints, positionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(ObservableList<MountPoint> mountPoints, int fromPosition, int toPosition, int itemCount) {
//                    Log.d(TAG, "onItemRangeMoved("+mountPoints+", "+fromPosition+", "+toPosition+", "+itemCount+")");
                }

                @Override
                public void onItemRangeRemoved(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {
//                    Log.d(TAG, "onItemRangeRemoved("+mountPoints+", "+positionStart+", "+itemCount+")");
                    removeMountPointTabItems(mountPoints, positionStart, itemCount);
                }
            };


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
        fsoViewModelAdapter.setPresenter(presenter);
        breadCrumbAdapter.setPresenter(presenter);
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

        fsoViewModelAdapter.onDestroy();
        breadCrumbAdapter.onDestroy();

        mountPoints.removeOnListChangedCallback(mountPointsListCallback);
        mountPoints = null;
    }

    @Override
    public void setViewModel(FileManagerViewModel viewModel) {
        binding.setViewModel(viewModel);
        fsoViewModelAdapter.setList(viewModel.fsoViewModels);
        breadCrumbAdapter.setList(viewModel.breadCrumbs);

        mountPoints = viewModel.mountPoints;
        mountPoints.addOnListChangedCallback(mountPointsListCallback);
        addMountPointTabItems(mountPoints, 0, mountPoints.size());
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
        binding.fsoList.setLayoutManager(mVerticalLinearLayoutManager);
        binding.fsoList.addItemDecoration(mDividerItemDecoration);
    }

    /**
     * @hide
     */
    @Override
    public void showAsGrid() {
        binding.fsoList.setLayoutManager(mGridLayoutManager);
        binding.fsoList.removeItemDecoration(mDividerItemDecoration);
    }

    /**
     * @hide
     */
    @Override
    public void setNavigationModeInternal(@IFileManagerNavigationMode int mode) {
        fsoViewModelAdapter.setNavigationMode(mode);
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
    public void selectMountPoint(MountPoint mountPoint) {
        for (int i=0; i<binding.mountPoints.getTabCount(); i++) {
            TabLayout.Tab tab = binding.mountPoints.getTabAt(i);
            MountPoint mp;
            if (tab != null) {
                mp = (MountPoint) tab.getTag();
                if (mp != null && mp.id() == mountPoint.id()) {
                    tab.select();
                    return;
                }
            }
        }
    }

    @Override
    public boolean goBack() {
        return getPresenter().onGoBack();
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
        fsoViewModelAdapter.notifyDataPayloadChanged();
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

        // Create data binding for wm_fm_file_manager_view_layout.xmlut.xml
        binding = WmFmFileManagerViewLayoutBinding.inflate(LayoutInflater.from(getContext()));

        // prepare LayoutManager's
        mVerticalLinearLayoutManager = new LinearLayoutManager(getContext());
        mGridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.wm_fm_default_grid_columns));
        // create item decoration for fso list
        mDividerItemDecoration = new DividerItemDecoration(binding.fsoList.getContext(), LinearLayoutManager.VERTICAL);

        // create adapters
        fsoViewModelAdapter = new FSOViewModelAdapter();
        breadCrumbAdapter = new BreadCrumbAdapter(R.layout.wm_fm_breadcrumb_item);
        breadCrumbAdapter.setOnLongClickListener(new IBreadCrumbListener() {
            @Override
            public boolean onBreadCrumbLongClick(BreadCrumb breadCrumb) {
                Toast.makeText(getContext(), breadCrumb.fullPath(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.fsoList.setHasFixedSize(true);
        binding.breadCrumbList.setHasFixedSize(true);

        // init breadcrumbs list
        binding.breadCrumbList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.fsoList.getContext(), LinearLayoutManager.HORIZONTAL);
        dividerItemDecoration.setDrawable(AndroidHelper.getVectorDrawable(getContext(), R.drawable.wm_fm_ic_chevron_right_24dp));
        binding.breadCrumbList.addItemDecoration(dividerItemDecoration);

        // optimization for fast scroll, but maybe i will remove it if will be bug with selected state
        binding.fsoList.setItemViewCacheSize(ITEM_VIEW_CACHE_SIZE);
        binding.fsoList.setDrawingCacheEnabled(true);
        binding.fsoList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        binding.setFsoAdapter(fsoViewModelAdapter);
        binding.setBreadCrumbAdapter(breadCrumbAdapter);

        binding.mountPoints.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getPresenter().onMountPointSelect((MountPoint) tab.getTag());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        addView(binding.getRoot());
    }

    private void addMountPointTabItems(List<MountPoint> mountPointList, int positionStart, int itemCount) {
        for (int i=positionStart; i<itemCount + positionStart; i++) {
            TabLayout.Tab tab = binding.mountPoints.newTab().setIcon(mountPointList.get(i).icon());
            tab.setTag(mountPointList.get(i));
            binding.mountPoints.addTab(tab, false);
        }
    }

    private void removeMountPointTabItems(List<MountPoint> mountPointList, int positionStart, int itemCount) {

        for (int i=positionStart; i<(positionStart + itemCount); i++) {
            binding.mountPoints.removeTabAt(positionStart);
        }

    }
}