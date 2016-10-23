package corp.wmsoft.android.lib.filemanager.adapters.base;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;


/**
 * An Adapter implementation that works with a {@link BaseDataBoundViewHolder}.
 * <p>
 * Although this version enforces a single item type, it can easily be extended to support multiple
 * view types.
 *
 * @param <T> The type of the binding class
 */
abstract public class SingleTypeDataBoundAdapter<T extends ViewDataBinding> extends BaseDataBoundAdapter<T> {

    @LayoutRes
    private final int mLayoutId;


    /**
     * Creates a SingleTypeDataBoundAdapter with the given item layout
     *
     * @param layoutId The layout to be used for items. It must use data binding.
     */
    public SingleTypeDataBoundAdapter(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    @Override
    public int getItemLayoutId(int position) {
        return mLayoutId;
    }
}
