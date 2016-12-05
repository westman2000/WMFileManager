package corp.wmsoft.android.lib.filemanager.adapters.holders;

import android.support.v7.widget.RecyclerView;

import corp.wmsoft.android.lib.filemanager.databinding.WmFmBreadcrumbItemBinding;
import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.ui.IBreadCrumbListener;
import corp.wmsoft.android.lib.filemanager.ui.IFileManagerViewContract;


/**
 *
 */
public class BreadCrumbItemViewHolder extends RecyclerView.ViewHolder {

    /**/
    public final WmFmBreadcrumbItemBinding binding;


    public BreadCrumbItemViewHolder(WmFmBreadcrumbItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(BreadCrumb breadCrumb, IFileManagerViewContract.Presenter presenter, IBreadCrumbListener longClickListener) {
        binding.setViewModel(breadCrumb);
        binding.setPresenter(presenter);
        binding.setListener(longClickListener);
    }

}