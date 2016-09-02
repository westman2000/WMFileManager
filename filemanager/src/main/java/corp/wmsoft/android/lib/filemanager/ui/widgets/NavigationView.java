package corp.wmsoft.android.lib.filemanager.ui.widgets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.FileSystemObjectAdapter;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.NavigationMode;
import corp.wmsoft.android.lib.mvpc.predefined.MVPCFrameLayout;
import corp.wmsoft.android.lib.mvpc.presenter.factory.IMVPCPresenterFactory;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 2:42 PM.<br/>
 */
public class NavigationView extends MVPCFrameLayout<INavigationViewContract.View, INavigationViewContract.Presenter>
        implements INavigationViewContract.View, View.OnClickListener {

    @Override
    public void showError(Error error) {

    }

    /**
     * An interface to communicate a request when the user choose a file.
     */
    public interface OnFilePickedListener {
        /**
         * Method invoked when a request when the user choose a file.
         *
         * @param fso The item choose
         */
        void onFilePicked(FileSystemObject fso);
    }

    /**
     * An interface to communicate a change of the current directory
     */
    public interface OnDirectoryChangedListener {
        /**
         * Method invoked when the current directory changes
         *
         * @param fso The newly active directory
         */
        void onDirectoryChanged(FileSystemObject fso);
    }

    /**/
    private RecyclerView mRecyclerView;
    /**/
    private FileSystemObjectAdapter mAdapter;
    /**/
    private OnFilePickedListener mOnFilePickedListener;
    /**/
    private OnDirectoryChangedListener mOnDirectoryChangedListener;



    public NavigationView(Context context) {
        super(context);
        init();
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected IMVPCPresenterFactory<INavigationViewContract.View, INavigationViewContract.Presenter> providePresenterFactory() {
        return new NavigationViewPresenterFactory();
    }

    @Override
    protected int provideUniqueIdentifier() {
        return 640344;
    }

    @Override
    public void showAsList() {
        // use a linear layout manager for simple and details mode
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void showAsGrid() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getResources().getInteger(R.integer.default_grid_columns)));
    }

    @Override
    public void setNavigationMode(@NavigationMode int mode) {
        mAdapter.setNavigationMode(mode);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void setData(List<FileSystemObject> data) {
        mAdapter.clearAndAddAll(data);
    }

    @Override
    public void showContent() {

    }

    @Override
    public void showNoDataView() {

    }

    @Override
    public void onClick(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        FileSystemObject fso = mAdapter.getItem(position);
        getPresenter().onFSOPicked(fso);
    }

    /**
     * Method that sets the listener for picked items
     *
     * @param onFilePickedListener The listener reference
     */
    public void setOnFilePickedListener(OnFilePickedListener onFilePickedListener) {
        this.mOnFilePickedListener = onFilePickedListener;
    }

    /**
     * Method that sets the listener for directory changes
     *
     * @param onDirectoryChangedListener The listener reference
     */
    public void setOnDirectoryChangedListener(OnDirectoryChangedListener onDirectoryChangedListener) {
        this.mOnDirectoryChangedListener = onDirectoryChangedListener;
    }

    /**
     * Method that initializes the view. This method loads all the necessary
     * information and create an appropriate layout for the view.
     */
    private void init() {

        mAdapter = new FileSystemObjectAdapter();
        mAdapter.setViewOnClickListener(this);

        mRecyclerView = new RecyclerView(getContext());
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        addView(mRecyclerView);
    }

}