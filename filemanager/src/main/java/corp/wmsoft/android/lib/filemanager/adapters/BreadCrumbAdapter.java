package corp.wmsoft.android.lib.filemanager.adapters;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.LayoutRes;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.adapters.base.BaseDataBoundViewHolder;
import corp.wmsoft.android.lib.filemanager.adapters.base.SingleTypeDataBoundAdapter;
import corp.wmsoft.android.lib.filemanager.databinding.WmFmBreadcrumbItemBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IBreadCrumbListener;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:09 PM.<br/>
 */
public class BreadCrumbAdapter extends SingleTypeDataBoundAdapter<WmFmBreadcrumbItemBinding> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::FMViewInternal";

    /**/
    private ObservableList<BreadCrumb> breadCrumbs = new ObservableArrayList<>();

    /**/
    private IFileManagerViewContract.Presenter presenter;

    /**/
    private IBreadCrumbListener onBreadCrumbLongClickListener;

    /**
     * Creates a SingleTypeDataBoundAdapter with the given item layout
     *
     * @param layoutId The layout to be used for items. It must use data binding.
     */
    public BreadCrumbAdapter(@LayoutRes int layoutId, IBreadCrumbListener listener) {
        super(layoutId);
        onBreadCrumbLongClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return breadCrumbs.size();
    }

    @Override
    protected void bindItem(BaseDataBoundViewHolder<WmFmBreadcrumbItemBinding> holder, int position, List<Object> payloads) {
        holder.binding.setViewModel(breadCrumbs.get(position));
        holder.binding.setPresenter(presenter);
        if (onBreadCrumbLongClickListener != null)
            holder.binding.setListener(onBreadCrumbLongClickListener);
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
        onBreadCrumbLongClickListener = null;
        breadCrumbs.removeOnListChangedCallback(callback);
        breadCrumbs = null;
        presenter = null;
    }

    private void smoothScrollToEnd() {
        if (getRecyclerView() != null && getItemCount() > 0)
            getRecyclerView().smoothScrollToPosition(getItemCount() - 1);
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
