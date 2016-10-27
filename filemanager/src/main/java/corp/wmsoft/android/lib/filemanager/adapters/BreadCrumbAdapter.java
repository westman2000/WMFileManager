package corp.wmsoft.android.lib.filemanager.adapters;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.adapters.holders.BreadCrumbItemViewHolder;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmBreadcrumbItemBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IBreadCrumbListener;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:09 PM.<br/>
 */
public class BreadCrumbAdapter extends RecyclerView.Adapter<BreadCrumbItemViewHolder> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private static final Object DB_PAYLOAD = new Object();
    /**/
    @Nullable
    private RecyclerView mRecyclerView;
    /**/
    private ObservableList<BreadCrumb> breadCrumbs = new ObservableArrayList<>();
    /**/
    private IFileManagerViewContract.Presenter presenter;
    /**/
    private IBreadCrumbListener onBreadCrumbLongClickListener;

    /**
     *
     */
    @Keep
    public BreadCrumbAdapter(IBreadCrumbListener listener) {
        onBreadCrumbLongClickListener = listener;
    }

    @Override
    @CallSuper
    public BreadCrumbItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        WmFmBreadcrumbItemBinding binding = WmFmBreadcrumbItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.addOnRebindCallback(mOnRebindCallback);
        return new BreadCrumbItemViewHolder(binding);
    }

    @Override
    public final void onBindViewHolder(BreadCrumbItemViewHolder holder, int position) {
        throw new IllegalArgumentException("just overridden to make final.");
    }

    @Override
    public final void onBindViewHolder(BreadCrumbItemViewHolder holder, int position, List<Object> payloads) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            holder.bind(breadCrumbs.get(position), presenter, onBreadCrumbLongClickListener);
        }
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return breadCrumbs.size();
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

    public void setList(ObservableList<BreadCrumb> list) {
        breadCrumbs = list;
        breadCrumbs.addOnListChangedCallback(callback);
    }

    public void setPresenter(IFileManagerViewContract.Presenter presenter) {
        this.presenter = presenter;
        notifyDataSetChanged();
    }

    public void onDestroy() {
        breadCrumbs.removeOnListChangedCallback(callback);
        onBreadCrumbLongClickListener = null;
        breadCrumbs = null;
        presenter = null;
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
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }

    private void smoothScrollToEnd() {
        if (mRecyclerView != null && getItemCount() > 0)
            mRecyclerView.smoothScrollToPosition(getItemCount() - 1);
    }

    /**
     *
     */
    private ObservableList.OnListChangedCallback<ObservableList<BreadCrumb>> callback =
        new ObservableList.OnListChangedCallback<ObservableList<BreadCrumb>>() {

            @Override
            public void onChanged(ObservableList<BreadCrumb> breadCrumbs) {
                notifyDataSetChanged();
                smoothScrollToEnd();
            }

            @Override
            public void onItemRangeChanged(ObservableList<BreadCrumb> breadCrumbs, int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart, itemCount);
                smoothScrollToEnd();
            }

            @Override
            public void onItemRangeInserted(ObservableList<BreadCrumb> breadCrumbs, int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart, itemCount);
                smoothScrollToEnd();
            }

            @Override
            public void onItemRangeMoved(ObservableList<BreadCrumb> breadCrumbs, int fromPosition, int toPosition, int itemCount) {
                notifyItemRangeRemoved(fromPosition, itemCount);
                smoothScrollToEnd();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<BreadCrumb> breadCrumbs, int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart, itemCount);
                smoothScrollToEnd();
            }
        };


}
