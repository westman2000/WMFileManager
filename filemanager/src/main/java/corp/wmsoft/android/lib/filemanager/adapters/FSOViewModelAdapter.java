package corp.wmsoft.android.lib.filemanager.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewBaseItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewDetailsItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewIconsItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewSimpleItemViewHolder;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewDetailsItemBinding;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewIconsItemBinding;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewSimpleItemBinding;
import corp.wmsoft.android.lib.filemanager.ui.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.IFileManagerViewContract;


/**
 *
 */
public class FSOViewModelAdapter extends RecyclerView.Adapter<NavigationViewBaseItemViewHolder> {

    /**/
    private ObservableList<FSOViewModel> fsoViewModels = new ObservableArrayList<>();

    /**/
    private LayoutInflater inflater;

    /**/
    private IFileManagerViewContract.Presenter presenter;

    /**/
    @IFileManagerNavigationMode
    private int mCurrentNavigationMode;


    public FSOViewModelAdapter() {
        mCurrentNavigationMode = IFileManagerNavigationMode.DETAILS;
    }

    @Override
    @CallSuper
    public NavigationViewBaseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        if (viewType == IFileManagerNavigationMode.DETAILS) {
            WmFmNavigationViewDetailsItemBinding detailsBinding = DataBindingUtil.inflate(inflater, R.layout.wm_fm_navigation_view_details_item, parent, false);
            return new NavigationViewDetailsItemViewHolder(detailsBinding);
        }

        if (viewType == IFileManagerNavigationMode.ICONS) {
            WmFmNavigationViewIconsItemBinding iconsBinding = DataBindingUtil.inflate(inflater, R.layout.wm_fm_navigation_view_icons_item, parent, false);
            return new NavigationViewIconsItemViewHolder(iconsBinding);
        }

        if (viewType == IFileManagerNavigationMode.SIMPLE) {
            WmFmNavigationViewSimpleItemBinding simpleBinding = DataBindingUtil.inflate(inflater, R.layout.wm_fm_navigation_view_simple_item, parent, false);
            return new NavigationViewSimpleItemViewHolder(simpleBinding);
        }

        return null;
    }

    @Override
    public final void onBindViewHolder(NavigationViewBaseItemViewHolder holder, int position) {
        holder.bind(fsoViewModels.get(position), presenter);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentNavigationMode;
    }

    @Override
    public int getItemCount() {
        return fsoViewModels.size();
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        fsoViewModels.removeOnListChangedCallback(callback);
        fsoViewModels = null;
        presenter = null;
    }

    public void setList(ObservableList<FSOViewModel> list) {
        fsoViewModels = list;
        fsoViewModels.addOnListChangedCallback(callback);
    }

    public void setNavigationMode(@IFileManagerNavigationMode int newMode) {
        // Check that it is really necessary change the mode
        if (this.mCurrentNavigationMode == newMode) return;
        this.mCurrentNavigationMode = newMode;
        notifyDataSetChanged();
    }

    public void setPresenter(IFileManagerViewContract.Presenter presenter) {
        this.presenter = presenter;
        notifyDataSetChanged();
    }

    public List<String> getFsoNames() {
        List<String> names = new ArrayList<>();

        for (FSOViewModel fsoViewModel : fsoViewModels) {
            names.add(fsoViewModel.fso.name());
        }

        return names;
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
