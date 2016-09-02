package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.FileSystemObjectAdapter;
import corp.wmsoft.android.lib.filemanager.util.DividerItemDecoration;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.mvpc.predefined.MVPCFrameLayout;
import corp.wmsoft.android.lib.mvpc.presenter.factory.IMVPCPresenterFactory;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 2:42 PM.<br/>
 */
public class FileManagerView extends MVPCFrameLayout<IFileManagerViewContract.View, IFileManagerViewContract.Presenter>
        implements IFileManagerViewContract.View, View.OnClickListener {

    /**/
    private final static String TAG = "FileManagerView";


    /**/
    private RecyclerView mRecyclerView;
    /**/
    private ProgressBar mProgressBar;
    /**/
    private FileSystemObjectAdapter mAdapter;
    /**/
    private IOnFileManagerEventListener mOnFileManagerEventListener;
    /**/
    private IOnFilePickedListener mOnFilePickedListener;
    /**/
    private IOnDirectoryChangedListener mOnDirectoryChangedListener;



    public FileManagerView(Context context) {
        super(context);
        Log.d(TAG, "FileManagerView.FileManagerView("+context+")");
        init();
    }

    public FileManagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "FileManagerView.FileManagerView("+context+", "+attrs+")");
        init();
    }

    public FileManagerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "FileManagerView.FileManagerView("+context+", "+attrs+", "+defStyleAttr+")");
        init();
    }

    public FileManagerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d(TAG, "FileManagerView.FileManagerView("+context+", "+attrs+", "+defStyleAttr+", "+defStyleRes+")");
        init();
    }

    @Override
    protected IMVPCPresenterFactory<IFileManagerViewContract.View, IFileManagerViewContract.Presenter> providePresenterFactory() {
        Log.d(TAG, "FileManagerView.providePresenterFactory()");
        return new FileManagerViewPresenterFactory();
    }

    @Override
    protected int provideUniqueIdentifier() {
        Log.d(TAG, "FileManagerView.provideUniqueIdentifier()");
        return 640344;
    }

    @Override
    public void showError(Error error) {
        Log.d(TAG, "FileManagerView.showError()");
    }

    @Override
    public void sendEvent(@IFileManagerEvent int event) {
        Log.d(TAG, "FileManagerView.sendEvent("+event+")");
        if (mOnFileManagerEventListener != null)
            mOnFileManagerEventListener.onFileManagerEvent(event);
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        Log.d(TAG, "FileManagerView.onExternalStoragePermissionsGranted()");
        getPresenter().onExternalStoragePermissionsGranted();
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {
        Log.d(TAG, "FileManagerView.onExternalStoragePermissionsNotGranted()");
    }

    @Override
    public void showAsList() {
        Log.d(TAG, "FileManagerView.showAsList()");
        // use a linear layout manager for simple and details mode
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void showAsGrid() {
        Log.d(TAG, "FileManagerView.showAsGrid()");
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.default_grid_columns)));
        mRecyclerView.addItemDecoration(null);
    }

    @Override
    public void setNavigationMode(@IFileManagerNavigationMode int mode) {
        Log.d(TAG, "FileManagerView.setNavigationMode("+mode+")");
        mAdapter.setNavigationMode(mode);
    }

    @Override
    public void showLoading() {
        Log.d(TAG, "FileManagerView.showLoading()");
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        Log.d(TAG, "FileManagerView.hideLoading()");
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setData(List<FileSystemObject> data) {
        Log.d(TAG, "FileManagerView.setData("+data+")");
        mAdapter.clearAndAddAll(data);
    }

    @Override
    public void showContent() {
        Log.d(TAG, "FileManagerView.showContent()");
    }

    @Override
    public void showNoDataView() {
        Log.d(TAG, "FileManagerView.showNoDataView()");
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "FileManagerView.onClick("+view+")");
        int position = mRecyclerView.getChildAdapterPosition(view);
        FileSystemObject fso = mAdapter.getItem(position);
        getPresenter().onFSOPicked(fso);
    }

    /**
     * Method that sets the listener for events
     *
     * @param onFileManagerEventListener The listener reference
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        this.mOnFileManagerEventListener = onFileManagerEventListener;
    }

    /**
     * Method that sets the listener for picked items
     *
     * @param onFilePickedListener The listener reference
     */
    public void setOnFilePickedListener(IOnFilePickedListener onFilePickedListener) {
        this.mOnFilePickedListener = onFilePickedListener;
    }

    /**
     * Method that sets the listener for directory changes
     *
     * @param onDirectoryChangedListener The listener reference
     */
    public void setOnDirectoryChangedListener(IOnDirectoryChangedListener onDirectoryChangedListener) {
        this.mOnDirectoryChangedListener = onDirectoryChangedListener;
    }

    /**
     * Method that initializes the view. This method loads all the necessary
     * information and create an appropriate layout for the view.
     */
    private void init() {
        Log.d(TAG, "FileManagerView.init()");



        mAdapter = new FileSystemObjectAdapter();
        mAdapter.setViewOnClickListener(this);

        mRecyclerView = new RecyclerView(getContext());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

        addView(mRecyclerView);

        createProgressBar();
    }

    private void createProgressBar() {
        mProgressBar = new ProgressBar(getContext());

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        mProgressBar.setLayoutParams(params);

        addView(mProgressBar);
    }
}