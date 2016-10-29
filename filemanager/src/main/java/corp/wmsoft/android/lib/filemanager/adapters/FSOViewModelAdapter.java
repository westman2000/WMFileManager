package corp.wmsoft.android.lib.filemanager.adapters;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewBaseItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewDetailsItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewIconsItemViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.holders.NavigationViewSimpleItemViewHolder;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewDetailsItemBinding;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewIconsItemBinding;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewSimpleItemBinding;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 *
 */
public class FSOViewModelAdapter extends RecyclerView.Adapter<NavigationViewBaseItemViewHolder> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private static final Object DB_PAYLOAD = new Object();

    /**/
    @Nullable
    private RecyclerView mRecyclerView;

    /**/
    private ObservableList<FSOViewModel> fsoViewModels = new ObservableArrayList<>();

    /**/
    private IFileManagerViewContract.Presenter presenter;

    /**/
    @IFileManagerNavigationMode
    private int mCurrentNavigationMode;


    @Keep
    public FSOViewModelAdapter() {
        mCurrentNavigationMode = IFileManagerNavigationMode.DETAILS;
    }

    @Override
    @CallSuper
    public NavigationViewBaseItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (mCurrentNavigationMode) {
            case IFileManagerNavigationMode.ICONS:
                WmFmNavigationViewIconsItemBinding iconsBinding = WmFmNavigationViewIconsItemBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                iconsBinding.addOnRebindCallback(mOnRebindCallback);
                return new NavigationViewIconsItemViewHolder(iconsBinding);
            case IFileManagerNavigationMode.SIMPLE:
                WmFmNavigationViewSimpleItemBinding simpleBinding = WmFmNavigationViewSimpleItemBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                simpleBinding.addOnRebindCallback(mOnRebindCallback);
                return new NavigationViewSimpleItemViewHolder(simpleBinding);
            case IFileManagerNavigationMode.DETAILS:
            case IFileManagerNavigationMode.UNDEFINED:
            default:
                WmFmNavigationViewDetailsItemBinding detailsBinding = WmFmNavigationViewDetailsItemBinding
                        .inflate(LayoutInflater.from(parent.getContext()), parent, false);
                detailsBinding.addOnRebindCallback(mOnRebindCallback);
                return new NavigationViewDetailsItemViewHolder(detailsBinding);
        }
    }

    @Override
    public final void onBindViewHolder(NavigationViewBaseItemViewHolder holder, int position, List<Object> payloads) {
//        Log.d(TAG, "onBindViewHolder("+position+", "+payloads+"): ");
        // when a VH is rebound to the same item, we don't have to call the setters
        if (hasNonDataBindingInvalidate(payloads)) {
            holder.bind(fsoViewModels.get(position), presenter);
            holder.binding.executePendingBindings();
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }

    }

    @Override
    public final void onBindViewHolder(NavigationViewBaseItemViewHolder holder, int position) {
//        Log.d(TAG, "onBindViewHolder("+position+"): ");
//        holder.bind(fsoViewModels.get(position), presenter);
//        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return fsoViewModels.size();
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = null;
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
     * This is used to block items from updating themselves. RecyclerView wants to know when an
     * item is invalidated and it prefers to refresh it via onRebind. It also helps with performance
     * since data binding will not update views that are not changed.
     */
    private final OnRebindCallback mOnRebindCallback = new OnRebindCallback() {
        @Override
        public boolean onPreBind(ViewDataBinding binding) {
            if (mRecyclerView == null || mRecyclerView.isComputingLayout()) {
                return true;
            }
            int childAdapterPosition = mRecyclerView.getChildAdapterPosition(binding.getRoot());
            if (childAdapterPosition == RecyclerView.NO_POSITION) {
                return true;
            }
            notifyItemChanged(childAdapterPosition, DB_PAYLOAD);
            return false;
        }
    };

    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            return true;
        }
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
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
