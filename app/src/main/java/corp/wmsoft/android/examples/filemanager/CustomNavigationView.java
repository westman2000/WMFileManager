package corp.wmsoft.android.examples.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.ui.widgets.mp.IMountPointsViewContract;
import corp.wmsoft.android.lib.filemanager.ui.widgets.mp.MountPointsViewPresenterFactory;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IOnFileManagerEventListener;
import corp.wmsoft.android.lib.mvpc.delegate.IMVPCDelegate;
import corp.wmsoft.android.lib.mvpc.delegate.MVPCDelegate;


/**
 * <br/>Created by WestMan2000 on 9/12/16 at 12:07 PM.<br/>
 */
@SuppressLint("LongLogTag")
public class CustomNavigationView extends NavigationView implements IMVPCDelegate.ICallback<IMountPointsViewContract.View, IMountPointsViewContract.Presenter>, IMountPointsViewContract.View {

    /**/
    private final static String TAG = "MountPointsViewCustomNavigationView";

    /**/
    private MVPCDelegate<IMountPointsViewContract.View, IMountPointsViewContract.Presenter> mMvpcDelegate;

    /**/
    private IOnFileManagerEventListener mOnFileManagerEventListener;
    /**/
    private OnMountPointSelected mOnMountPointSelected;



    public interface OnMountPointSelected {

        void onMountPointSelected(MountPoint mountPoint);

    }



    public CustomNavigationView(Context context) {
        super(context);
        init(context);
    }

    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @CallSuper
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //noinspection unchecked
        getMvpсDelegate().onAttachView(this, this);
    }

    @CallSuper
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getMvpсDelegate().onDetachView();
    }

    @Override
    public void onInitializePresenter(IMountPointsViewContract.Presenter presenter) {
        // empty
    }

    @Override
    public void onDestroyPresenter() {
        // empty
    }

    @Override
    public void sendEvent(@IFileManagerEvent int event) {
        if (mOnFileManagerEventListener != null)
            mOnFileManagerEventListener.onFileManagerEvent(event);
    }

    @Override
    public void onExternalStoragePermissionsGranted() {
        Log.d(TAG, "onExternalStoragePermissionsGranted()");
        getPresenter().onExternalStoragePermissionsGranted();
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {
        Log.d(TAG, "onExternalStoragePermissionsNotGranted()");
    }

    @Override
    public void selectMountPoint(MountPoint mountPoint) {

        Log.d(TAG, "selectMountPoint("+mountPoint+")");

        MenuItem menuItem = getMenu().findItem(mountPoint.getId());
        if (menuItem != null) {
            menuItem.setChecked(true);
            if (mOnMountPointSelected != null)
                mOnMountPointSelected.onMountPointSelected(mountPoint);
        }
    }

    @Override
    public void showLoading() {
        Log.d(TAG, "showLoading()");
    }

    @Override
    public void hideLoading() {
        Log.d(TAG, "hideLoading()");
    }

    @Override
    public void setData(List<MountPoint> data) {

        Log.d(TAG, "setData("+data+")");

        for (MountPoint mountPoint : data) {
            MenuItem menuItem = getMenu().add(R.id.mount_points, mountPoint.getId(), Menu.NONE, mountPoint.getDescription()+"("+mountPoint.isPrimary()+")");
            menuItem.setIcon(mountPoint.getIconResId());
        }

        getMenu().setGroupCheckable(R.id.mount_points, true, true);
    }

    @Override
    public void showContent() {
        Log.d(TAG, "showContent()");
    }

    @Override
    public void showNoDataView() {

    }

    @Override
    public void showError(Error error) {
        Log.d(TAG, "showError()");
    }

    public void setOnMountPointSelected(OnMountPointSelected onMountPointSelectedListener) {
        mOnMountPointSelected = onMountPointSelectedListener;
    }

    public void onMountPointSelect(int itemId) {
        getPresenter().onSelectMountPoint(itemId);
    }

    /**
     * Method that sets the listener for events
     *
     * @param onFileManagerEventListener The listener reference
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        mOnFileManagerEventListener = onFileManagerEventListener;
    }

    private IMountPointsViewContract.Presenter getPresenter() {
        return getMvpсDelegate().getPresenter();
    }

    /**
     * @return The {@link MVPCDelegate} being used by this Fragment.
     */
    private MVPCDelegate<IMountPointsViewContract.View, IMountPointsViewContract.Presenter> getMvpсDelegate() {
        if (mMvpcDelegate == null) {
            mMvpcDelegate = new MVPCDelegate<>();
        }
        return mMvpcDelegate;
    }

    private void init(Context context) {
        getMvpсDelegate().onCreate(
                context,
                ((AppCompatActivity)context).getSupportLoaderManager(),
                new MountPointsViewPresenterFactory(),
                6597893,
                this);
    }

}
