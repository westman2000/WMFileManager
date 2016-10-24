package corp.wmsoft.android.lib.filemanager.adapters;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.BR;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.base.BaseDataBoundAdapter;
import corp.wmsoft.android.lib.filemanager.adapters.base.BaseDataBoundViewHolder;
import corp.wmsoft.android.lib.filemanager.ui.widgets.FixedSizeImageView;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:09 PM.<br/>
 */
public class FSOViewModelAdapter extends BaseDataBoundAdapter {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private ObservableList<FSOViewModel> fsoViewModels = new ObservableArrayList<>();

    /**/
    private IFileManagerViewContract.Presenter presenter;

    /**/
    @IFileManagerNavigationMode
    private int mCurrentNavigationMode;

    public FSOViewModelAdapter() {
        mCurrentNavigationMode = IFileManagerNavigationMode.DETAILS;
    }

    @Override
    public int getItemCount() {
        return fsoViewModels.size();
    }

    @Override
    protected void bindItem(BaseDataBoundViewHolder holder, int position, List payloads) {
        holder.binding.setVariable(BR.viewModel, fsoViewModels.get(position));
        holder.binding.setVariable(BR.presenter, presenter);
    }

    @Override
    public int getItemLayoutId(int position) {
        switch (mCurrentNavigationMode) {
            case IFileManagerNavigationMode.ICONS:
                return R.layout.wm_fm_navigation_view_icons_item;
            case IFileManagerNavigationMode.SIMPLE:
                return R.layout.wm_fm_navigation_view_simple_item;
            case IFileManagerNavigationMode.DETAILS:
            case IFileManagerNavigationMode.UNDEFINED:
            default:
                return R.layout.wm_fm_navigation_view_details_item;
        }
    }

    @BindingAdapter({"app:iconByFso"})
    public static void iconByFso(FixedSizeImageView view, FSOViewModel fsoViewModel) {
        view.setImageByFso(fsoViewModel);
    }

    public void setList(ObservableList<FSOViewModel> list) {
        fsoViewModels = list;
        fsoViewModels.addOnListChangedCallback(callback);
    }

    public void onDestroy() {
        fsoViewModels.removeOnListChangedCallback(callback);
        fsoViewModels = null;
        presenter = null;
    }

    public void setNavigationMode(@IFileManagerNavigationMode int newMode) {
        //Check that it is really necessary change the mode
        if (this.mCurrentNavigationMode == newMode) return;
        this.mCurrentNavigationMode = newMode;
        notifyDataSetChanged();
    }

    public void setPresenter(IFileManagerViewContract.Presenter presenter) {
        this.presenter = presenter;
        notifyDataSetChanged();
    }

    public void notifyDataPayloadChanged() {
        notifyItemRangeChanged(0, getItemCount(), new Object());
    }


    /**
     *
     */
    private ObservableList.OnListChangedCallback<ObservableList<FSOViewModel>> callback =
        new ObservableList.OnListChangedCallback<ObservableList<FSOViewModel>>() {

            @Override
            public void onChanged(ObservableList<FSOViewModel> contactViewModels) {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(ObservableList<FSOViewModel> fsoViewModels, int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(ObservableList<FSOViewModel> fsoViewModels, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(ObservableList<FSOViewModel> fsoViewModels, int fromPosition, int toPosition, int itemCount) {
                notifyItemRangeRemoved(fromPosition, itemCount);
            }

            @Override
            public void onItemRangeRemoved(ObservableList<FSOViewModel> fsoViewModels, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        };

}
