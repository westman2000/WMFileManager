package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.IOnChooseDirectoryListener;
import corp.wmsoft.android.lib.filemanager.IOnDirectoryChangedListener;
import corp.wmsoft.android.lib.filemanager.IOnFilePickedListener;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.BreadCrumbAdapter;
import corp.wmsoft.android.lib.filemanager.adapters.FSOViewModelAdapter;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmFileManagerViewLayoutBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.util.AndroidHelper;
import corp.wmsoft.android.lib.filemanager.util.PermissionUtil;
import corp.wmsoft.android.lib.mvpcrx.predefined.MVPCSupportDialogFragment;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 2:42 PM.<br/>
 *
 */
public class FileManagerFragment extends MVPCSupportDialogFragment<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> implements IFileManagerViewContract.View, IBreadCrumbListener {

    /**/
    @SuppressWarnings("unused")
    public static final String TAG = "wmfm::FMViewInternal";

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
    private IOnFilePickedListener mOnFilePickedListener;
    /**/
    private IOnChooseDirectoryListener mOnChooseDirectoryListener;
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
                    removeMountPointTabItems(positionStart, itemCount);
                }
            };


    public static void replaceInFragmentManager(FragmentManager fragmentManager, @IdRes int containerId) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.replace(containerId, FileManagerFragment.newInstance(), FileManagerFragment.TAG)
          .commit();
    }

    public static void showAsDialog(FragmentManager fragmentManager) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = FileManagerFragment.newInstance();
        newFragment.show(ft, TAG);
    }

    private static FileManagerFragment newInstance() {
        FileManagerFragment frag = new FileManagerFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    protected IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> providePresenterFactory() {
        return new FileManagerViewPresenterFactory();
    }

    @Override
    public void onInitializePresenter(IFileManagerViewContract.Presenter presenter) {
        Log.d(TAG, "onInitializePresenter() ["+fsoViewModelAdapter+"] ["+breadCrumbAdapter+"]");
        if (fsoViewModelAdapter != null)
            fsoViewModelAdapter.setPresenter(presenter);
        if (breadCrumbAdapter != null)
            breadCrumbAdapter.setPresenter(presenter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IOnChooseDirectoryListener)
            mOnChooseDirectoryListener = (IOnChooseDirectoryListener) context;

        if (context instanceof IOnFilePickedListener)
            mOnFilePickedListener = (IOnFilePickedListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mOnChooseDirectoryListener = null;
        mOnFilePickedListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create adapters
        fsoViewModelAdapter = new FSOViewModelAdapter();
        breadCrumbAdapter = new BreadCrumbAdapter(R.layout.wm_fm_breadcrumb_item, this);

        // prepare LayoutManager's
        mVerticalLinearLayoutManager = new LinearLayoutManager(getContext());
        breadCrumbsLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mGridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.wm_fm_default_grid_columns));
        // create item decoration for fso list
        mDividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        // создаем кастомный разделитель из картинки в виде стрелочки
        breadCrumbDividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL);
        breadCrumbDividerItemDecoration.setDrawable(AndroidHelper.getVectorDrawable(getContext(), R.drawable.wm_fm_ic_chevron_right_24dp));
    }

    /**
     * Method that initializes the view. This method loads all the necessary
     * information and create an appropriate layout for the view.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getShowsDialog()) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        // Create data binding
        binding = WmFmFileManagerViewLayoutBinding.inflate(inflater, container, false);
        setupView();
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Create data binding
        binding = WmFmFileManagerViewLayoutBinding.inflate(getActivity().getLayoutInflater(), null, false);

        // small hack for dialog only
        binding.fsoList.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.wm_fm_breadcrumb_item_max_size));
        binding.appBar.setBackgroundColor(Color.WHITE);

        setupView();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_fso_folder_24dp)
                .setTitle(R.string.wm_fm_app_name)
                .setView(binding.getRoot())
                .setNegativeButton(android.R.string.cancel, null);

        if (mOnChooseDirectoryListener != null)
            builder.setPositiveButton("Choose", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mOnChooseDirectoryListener.onDirectorySelected(getPresenter().getCurrentDir());
                }
            });

        return  builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.file_manager, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (getNavigationMode() == IFileManagerNavigationMode.ICONS)
            menu.findItem(R.id.action_icons).setChecked(true);
        else if (getNavigationMode() == IFileManagerNavigationMode.SIMPLE)
            menu.findItem(R.id.action_simple).setChecked(true);
        else if (getNavigationMode() == IFileManagerNavigationMode.DETAILS)
            menu.findItem(R.id.action_details).setChecked(true);

        if (getFileTimeFormat() == IFileManagerFileTimeFormat.SYSTEM)
            menu.findItem(R.id.action_system).setChecked(true);
        else if (getFileTimeFormat() == IFileManagerFileTimeFormat.LOCALE)
            menu.findItem(R.id.action_locale).setChecked(true);
        else if (getFileTimeFormat() == IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS)
            menu.findItem(R.id.action_ddmmyyyy).setChecked(true);
        else if (getFileTimeFormat() == IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS)
            menu.findItem(R.id.action_mmddyyyy).setChecked(true);
        else if (getFileTimeFormat() == IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS)
            menu.findItem(R.id.action_yyyymmdd).setChecked(true);

        menu.findItem(R.id.action_is_show_hidden).setChecked(isShowHidden());
        menu.findItem(R.id.action_dirs_first).setChecked(isShowDirsFirst());
        menu.findItem(R.id.action_show_thumbs).setChecked(isShowThumbs());

        if (getSortMode() == IFileManagerSortMode.NAME_ASC)
            menu.findItem(R.id.action_sort_by_name_asc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.NAME_DESC)
            menu.findItem(R.id.action_sort_by_name_desc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.DATE_ASC)
            menu.findItem(R.id.action_sort_by_date_asc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.DATE_DESC)
            menu.findItem(R.id.action_sort_by_date_desc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.SIZE_ASC)
            menu.findItem(R.id.action_sort_by_size_asc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.SIZE_DESC)
            menu.findItem(R.id.action_sort_by_size_desc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.TYPE_ASC)
            menu.findItem(R.id.action_sort_by_type_asc).setChecked(true);
        else if (getSortMode() == IFileManagerSortMode.TYPE_DESC)
            menu.findItem(R.id.action_sort_by_type_desc).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_icons) {
            setNavigationMode(IFileManagerNavigationMode.ICONS);
            return true;
        } else if (id == R.id.action_simple) {
            setNavigationMode(IFileManagerNavigationMode.SIMPLE);
            return true;
        } else if (id == R.id.action_details) {
            setNavigationMode(IFileManagerNavigationMode.DETAILS);
            return true;
        } else if (id == R.id.action_system) {
            setTimeFormat(IFileManagerFileTimeFormat.SYSTEM);
            return true;
        } else if (id == R.id.action_locale) {
            setTimeFormat(IFileManagerFileTimeFormat.LOCALE);
            return true;
        } else if (id == R.id.action_ddmmyyyy) {
            setTimeFormat(IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS);
            return true;
        } else if (id == R.id.action_mmddyyyy) {
            setTimeFormat(IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS);
            return true;
        } else if (id == R.id.action_yyyymmdd) {
            setTimeFormat(IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS);
            return true;
        } else if (id == R.id.action_is_show_hidden) {
            item.setChecked(!item.isChecked());
            setShowHidden(item.isChecked());
            return true;
        } else if (id == R.id.action_dirs_first) {
            item.setChecked(!item.isChecked());
            setShowDirsFirst(item.isChecked());
            return true;
        } else if (id == R.id.action_show_thumbs) {
            item.setChecked(!item.isChecked());
            setShowThumbs(item.isChecked());
            return true;
        } else if (id == R.id.action_sort_by_name_asc) {
            setSortMode(IFileManagerSortMode.NAME_ASC);
            return true;
        } else if (id == R.id.action_sort_by_name_desc) {
            setSortMode(IFileManagerSortMode.NAME_DESC);
            return true;
        } else if (id == R.id.action_sort_by_date_asc) {
            setSortMode(IFileManagerSortMode.DATE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_date_desc) {
            setSortMode(IFileManagerSortMode.DATE_DESC);
            return true;
        } else if (id == R.id.action_sort_by_size_asc) {
            setSortMode(IFileManagerSortMode.SIZE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_size_desc) {
            setSortMode(IFileManagerSortMode.SIZE_DESC);
            return true;
        } else if (id == R.id.action_sort_by_type_asc) {
            setSortMode(IFileManagerSortMode.TYPE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_type_desc) {
            setSortMode(IFileManagerSortMode.TYPE_DESC);
            return true;
        }


        return super.onOptionsItemSelected(item);
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
    public void onDestroyView() {
        super.onDestroyView();

        binding.setFsoAdapter(null);
        binding.setBreadCrumbAdapter(null);
        fsoViewModelAdapter.onDestroy();
        breadCrumbAdapter.onDestroy();

        binding.mountPoints.removeOnTabSelectedListener(onTabSelectedListener);

        mountPoints.removeOnListChangedCallback(mountPointsListCallback);
        mountPoints = null;
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
    public void onExternalStoragePermissionsGranted() {
        getPresenter().onExternalStoragePermissionsGranted();
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {
    }

    @Override
    public void showAsList() {
        // use a linear layout manager for simple and details mode
        binding.fsoList.setLayoutManager(mVerticalLinearLayoutManager);
        binding.fsoList.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void showAsGrid() {
        binding.fsoList.setLayoutManager(mGridLayoutManager);
        binding.fsoList.removeItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void setNavigationModeInternal(@IFileManagerNavigationMode int mode) {
        fsoViewModelAdapter.setNavigationMode(mode);
    }

    @Override
    public void filePicked(String file) {
        if (mOnFilePickedListener != null) {
            mOnFilePickedListener.onFilePicked(file);
            this.dismiss();
        }
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
        Toast.makeText(getContext(), breadCrumb.fullPath(), Toast.LENGTH_SHORT).show();
        return true;
    }

    public void setTimeFormat(@IFileManagerFileTimeFormat int format) {
        getPresenter().onSetTimeFormat(format);
    }

    @IFileManagerFileTimeFormat
    public int getFileTimeFormat() {
        return getPresenter().getCurrentFileTimeFormat();
    }

    public void setNavigationMode(@IFileManagerNavigationMode int mode) {
        getPresenter().changeViewMode(mode);
    }

    @IFileManagerNavigationMode
    public int getNavigationMode() {
        return getPresenter().getCurrentMode();
    }

    public boolean isShowHidden() {
        return getPresenter().isShowHidden();
    }

    public void setShowHidden(boolean isVisible) {
        getPresenter().setShowHidden(isVisible);
    }

    public boolean isShowDirsFirst() {
        return getPresenter().isShowDirsFirst();
    }

    public void setShowDirsFirst(boolean isDirsFirst) {
        getPresenter().setShowDirsFirst(isDirsFirst);
    }

    public boolean isShowThumbs() {
        return getPresenter().isShowThumbs();
    }

    public void setShowThumbs(boolean isShowThumbs) {
        getPresenter().setShowThumbs(isShowThumbs);
        fsoViewModelAdapter.notifyDataPayloadChanged();
    }

    public void setSortMode(@IFileManagerSortMode int mode) {
        getPresenter().setSortMode(mode);
    }

    @IFileManagerSortMode
    public int getSortMode() {
        return getPresenter().getSortMode();
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
     * {@link FileManagerFragment#mountPoints} то достаточно знать positionStart и itemCount
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void askExternalStoragePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // Display a SnackBar with an explanation and a button to trigger the request.
                // TODO - move to strings
                Snackbar.make(binding.getRoot(), "To browse files, need access to file system", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestPermissions(
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
                requestPermissions(
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
}