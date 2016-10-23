package corp.wmsoft.android.lib.filemanager.ui.widgets.mp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IOnMountPointSelected;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.IOnFileManagerEventListener;
import corp.wmsoft.android.lib.mvpcrx.predefined.MVPCLinearLayout;
import corp.wmsoft.android.lib.mvpcrx.presenter.factory.IMVPCPresenterFactory;


/**
 * <p>Created by WestMan2000 on 9/26/16. <p>
 */
@Deprecated
public class MountPointViewInternal extends MVPCLinearLayout<IMountPointsViewContract.View, IMountPointsViewContract.Presenter> implements IMountPointsViewContract.View {

    /**/
    private IOnFileManagerEventListener mOnFileManagerEventListener;
    /**/
    private IOnMountPointSelected mOnMountPointSelectedListener;


    public MountPointViewInternal(Context context) {
        super(context);
    }

    public MountPointViewInternal(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MountPointViewInternal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MountPointViewInternal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected IMVPCPresenterFactory<IMountPointsViewContract.View, IMountPointsViewContract.Presenter> providePresenterFactory() {
        return new MountPointsViewPresenterFactory();
    }

    @Override
    protected int provideUniqueIdentifier() {
        return 42522308;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mOnFileManagerEventListener == null)
            throw new RuntimeException("IOnFileManagerEventListener not set! Call MountPointView.setOnFileManagerEventListener() to set listener.");
    }

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
    public void selectMountPoint(MountPoint mountPoint, boolean fireEvent) {
        int childCount = this.getChildCount();

        for (int i=0; i<childCount; i++) {
            AppCompatImageButton imageButton = (AppCompatImageButton) getChildAt(i);
            int mountPointId = (int) imageButton.getTag();
            if (mountPointId == mountPoint.id()) {
                // select it
                imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.wm_fm_colorAccent));

                if (mOnMountPointSelectedListener != null && fireEvent)
                    mOnMountPointSelectedListener.onMountPointSelected(mountPoint);
            } else {
                // unselect
                imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.wm_fm_colorPrimary));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mOnFileManagerEventListener = null;
        mOnMountPointSelectedListener = null;
    }

    @Override
    public void setData(List<MountPoint> data) {
        this.removeAllViews();

        for (MountPoint mountPoint : data) {
            AppCompatImageButton imageButton = new AppCompatImageButton(getContext());
            imageButton.setTag(mountPoint.id());
            imageButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.wm_fm_colorPrimary));
            imageButton.setEnabled(mountPoint.isMounted());
            imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mountPointId = (int) v.getTag();
                    getPresenter().onSelectMountPoint(mountPointId);
                }
            });

            this.addView(imageButton);
        }
    }

    @Override
    public void onExternalStoragePermissionsNotGranted() {
        // empty
    }

    @Override
    public void showLoading() {
        // empty
    }

    @Override
    public void hideLoading() {
        // empty
    }

    @Override
    public void showContent() {
        // empty
    }

    @Override
    public void showNoDataView() {
        // empty
    }

    @Override
    public void showError(Error error) {
        // empty
    }

    /**
     * Method that sets the listener for events
     *
     * @param onFileManagerEventListener The listener reference
     */
    public void setOnFileManagerEventListener(IOnFileManagerEventListener onFileManagerEventListener) {
        this.mOnFileManagerEventListener = onFileManagerEventListener;
    }

    public void setOnMountPointSelected(IOnMountPointSelected onMountPointSelectedListener) {
        this.mOnMountPointSelectedListener = onMountPointSelectedListener;
    }
}
