package corp.wmsoft.android.lib.filemanager.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.BreadCrumbAdapter;
import corp.wmsoft.android.lib.filemanager.adapters.FSOViewModelAdapter;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmFileManagerViewLayoutBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.AndroidHelper;
import corp.wmsoft.android.lib.filemanager.util.PermissionUtil;
import corp.wmsoft.android.lib.mvpcrx.predefined.MVPCAppCompatActivity;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 *
 */
public class FileManagerActivity extends MVPCAppCompatActivity<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> implements IFileManagerViewContract.View, IBreadCrumbListener {

    /**/
    private static final String TAG = "wmfm::Activity";

    /**/
    public static final String EXTRA_TITLE = "EXTRA_TITLE";

    /**/
    private static final int FILE_MANAGER_PERMISSIONS_REQUEST   = 123;

    /**/
    private WmFmFileManagerViewLayoutBinding binding;
    /**/
    private LinearLayoutManager mVerticalLinearLayoutManager;
    private LinearLayoutManager breadCrumbsLinearLayoutManager;
    /**/
    private GridLayoutManager mGridLayoutManager;
    /**/
    private DividerItemDecoration mDividerItemDecoration;
    private DividerItemDecoration breadCrumbDividerItemDecoration;
    /**/
    private FSOViewModelAdapter fsoViewModelAdapter;
    /**/
    private BreadCrumbAdapter breadCrumbAdapter;
    /**/
    private ObservableList<MountPoint> mountPoints;
    /**/
    private ObservableList.OnListChangedCallback<ObservableList<MountPoint>> mountPointsListCallback =
            new ObservableList.OnListChangedCallback<ObservableList<MountPoint>>() {
                @Override
                public void onChanged(ObservableList<MountPoint> mountPoints) {}
                @Override
                public void onItemRangeChanged(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {}
                @Override
                public void onItemRangeMoved(ObservableList<MountPoint> mountPoints, int fromPosition, int toPosition, int itemCount) {}

                @Override
                public void onItemRangeInserted(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {
                    addMountPointTabItems(mountPoints, positionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(ObservableList<MountPoint> mountPoints, int positionStart, int itemCount) {
                    removeMountPointTabItems(positionStart, itemCount);
                }
            };


    public static void startAsDialogActivity(Context context, String title) {
        Intent intent = new Intent(context, FileManagerActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.wm_fm_file_manager_view_layout);

        // create adapters
        fsoViewModelAdapter = new FSOViewModelAdapter();
        breadCrumbAdapter = new BreadCrumbAdapter(this);

        // prepare LayoutManager's
        mVerticalLinearLayoutManager = new LinearLayoutManager(this);
        breadCrumbsLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.wm_fm_default_grid_columns));
        // create item decoration for fso list
        mDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        // создаем кастомный разделитель из картинки в виде стрелочки
        breadCrumbDividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL);
        breadCrumbDividerItemDecoration.setDrawable(AndroidHelper.getVectorDrawable(this, R.drawable.wm_fm_ic_chevron_right_24dp));

        setupView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding.setFsoAdapter(null);
        binding.setBreadCrumbAdapter(null);
        fsoViewModelAdapter.onDestroy();
        breadCrumbAdapter.onDestroy();

        binding.mountPoints.removeOnTabSelectedListener(onTabSelectedListener);

        mountPoints.removeOnListChangedCallback(mountPointsListCallback);
        mountPoints = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FILE_MANAGER_PERMISSIONS_REQUEST) {
            // We have requested multiple permissions for storage, so all of them need to be checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                getPresenter().onExternalStoragePermissionsGranted();
            } else {
                getPresenter().onExternalStoragePermissionsNotGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> providePresenterFactory() {
        return new FileManagerViewPresenterFactory();
    }

    @Override
    public void onInitializePresenter(IFileManagerViewContract.Presenter presenter) {
        if (fsoViewModelAdapter != null)
            fsoViewModelAdapter.setPresenter(presenter);
        if (breadCrumbAdapter != null)
            breadCrumbAdapter.setPresenter(presenter);
    }

    @Override
    public void askForExternalStoragePermission() {
        askExternalStoragePermission();
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

    @Override
    public void showAsList() {
        // use a linear layout manager for simple and details mode
        binding.fsoList.setLayoutManager(mVerticalLinearLayoutManager);
//        binding.fsoList.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void showAsGrid() {
        binding.fsoList.setLayoutManager(mGridLayoutManager);
//        binding.fsoList.removeItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void setNavigationModeInternal(@IFileManagerNavigationMode int mode) {
        fsoViewModelAdapter.setNavigationMode(mode);
    }

    @Override
    public void filePicked(String file) {
    }

    @Override
    public void directoryChanged(String dir) {
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

    @Override
    public boolean onBreadCrumbLongClick(BreadCrumb breadCrumb) {
        Toast.makeText(this, breadCrumb.fullPath(), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void setNavigationMode(@IFileManagerNavigationMode int mode) {
        getPresenter().changeViewMode(mode);
    }

    @IFileManagerNavigationMode
    public int getNavigationMode() {
        return getPresenter().getCurrentMode();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void askExternalStoragePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // Display a SnackBar with an explanation and a button to trigger the request.
                // TODO - move to strings
                Snackbar.make(binding.getRoot(), "To browse files, need access to file system", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(FileManagerActivity.this,
                                        new String[]{
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        },
                                        FILE_MANAGER_PERMISSIONS_REQUEST);
                            }
                        })
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(FileManagerActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        FILE_MANAGER_PERMISSIONS_REQUEST);
            }
        } else {
            getPresenter().onExternalStoragePermissionsGranted();
        }
    }

    private void setupView() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        binding.fsoList.setHasFixedSize(true);
        binding.breadCrumbList.setHasFixedSize(true);

        // init breadcrumbs list
        binding.breadCrumbList.setLayoutManager(breadCrumbsLinearLayoutManager);
        binding.breadCrumbList.addItemDecoration(breadCrumbDividerItemDecoration);

        // передаем адаптеры в биндинг
        binding.setFsoAdapter(fsoViewModelAdapter);
        binding.setBreadCrumbAdapter(breadCrumbAdapter);

        binding.mountPoints.addOnTabSelectedListener(onTabSelectedListener);
    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            getPresenter().onMountPointSelect((MountPoint) tab.getTag());
        }
        @Override public void onTabUnselected(TabLayout.Tab tab) {}
        @Override public void onTabReselected(TabLayout.Tab tab) {}
    };

    /**
     * Add a tab to this layout. The tab will be added in order as their came in list
     * <p>
     * Но не выделяем ни какой таб, это сделает презентер
     *
     * @param mountPointList список из которого берем табя для добавления
     * @param positionStart с какой позиции списка начинаем выбирать табы для добавления
     * @param itemCount кол-во табов для добавления
     */
    private void addMountPointTabItems(List<MountPoint> mountPointList, int positionStart, int itemCount) {
        for (int i = positionStart; i<itemCount + positionStart; i++) {
            TabLayout.Tab tab = binding.mountPoints.newTab().setIcon(mountPointList.get(i).icon());
            tab.setTag(mountPointList.get(i));
            binding.mountPoints.addTab(tab, false);
        }
    }

    /**
     * Remove a tab from the layout. Т.к. они расположены в соответствии с их позициями в
     * {@link FileManagerActivity#mountPoints} то достаточно знать positionStart и itemCount
     * <p>
     * If the removed tab was selected it will be deselected and another tab will be selected
     * if present.
     *
     * @param positionStart начальная позиция для удаления
     * @param itemCount кол-во для удаления
     */
    private void removeMountPointTabItems(int positionStart, int itemCount) {

        for (int i=positionStart; i<(positionStart + itemCount); i++) {
            binding.mountPoints.removeTabAt(positionStart);
        }

    }
}
