package corp.wmsoft.android.lib.filemanager.adapters.base;

import android.databinding.OnRebindCallback;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;


/**
 * A reference implementation for an adapter that wants to use data binding "the right way". It
 * works with {@link BaseDataBoundViewHolder}.
 * <p>
 * It listens for layout invalidations and notifies RecyclerView about them before views refresh
 * themselves. It also avoids invalidating the full item when data in the bound item dispatches
 * proper notify events.
 * <p>
 * This class uses layout id as the item type.
 * <p>
 * It can be used for both single type lists and multiple type lists.
 * <p>
 * https://github.com/google/android-ui-toolkit-demos/blob/master/DataBinding/DataBoundRecyclerView/app/src/main/java/com/example/android/databoundrecyclerview/BaseDataBoundAdapter.java
 * <p>
 * @param <T> The type of the ViewDataBinding class. Can be ommitted in multiple-binding-type use
 *           case.
 */
abstract public class BaseDataBoundAdapter<T extends ViewDataBinding> extends RecyclerView.Adapter<BaseDataBoundViewHolder<T>> {

    /**/
    private static final Object DB_PAYLOAD = new Object();
    /**/
    @Nullable
    private RecyclerView mRecyclerView;


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

    @Override
    @CallSuper
    public BaseDataBoundViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseDataBoundViewHolder<T> vh = BaseDataBoundViewHolder.create(parent, viewType);
        vh.binding.addOnRebindCallback(mOnRebindCallback);
        return vh;
    }

    @Override
    public final void onBindViewHolder(BaseDataBoundViewHolder<T> holder, int position, List<Object> payloads) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (payloads.isEmpty() || hasNonDataBindingInvalidate(payloads)) {
            bindItem(holder, position, payloads);
        }
        holder.binding.executePendingBindings();
    }

    /**
     * Override this method to handle binding your items into views
     *
     * @param holder The ViewHolder that has the binding instance
     * @param position The position of the item in the adapter
     * @param payloads The payloads that were passed into the onBind method
     */
    protected abstract void bindItem(BaseDataBoundViewHolder<T> holder, int position, List<Object> payloads);

    private boolean hasNonDataBindingInvalidate(List<Object> payloads) {
        for (Object payload : payloads) {
            if (payload != DB_PAYLOAD) {
                return true;
            }
        }
        return false;
    }

    @Override
    public final void onBindViewHolder(BaseDataBoundViewHolder<T> holder, int position) {
        throw new IllegalArgumentException("just overridden to make final.");
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

    @Override
    public final int getItemViewType(int position) {
        return getItemLayoutId(position);
    }

    @LayoutRes
    abstract public int getItemLayoutId(int position);
}