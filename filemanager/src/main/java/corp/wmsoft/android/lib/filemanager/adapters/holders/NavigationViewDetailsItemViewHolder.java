package corp.wmsoft.android.lib.filemanager.adapters.holders;

import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewDetailsItemBinding;
import corp.wmsoft.android.lib.filemanager.ui.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.IFileManagerViewContract;


/**
 *
 */
public class NavigationViewDetailsItemViewHolder extends NavigationViewBaseItemViewHolder {


    public NavigationViewDetailsItemViewHolder(WmFmNavigationViewDetailsItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(FSOViewModel fsoViewModel, IFileManagerViewContract.Presenter presenter) {
        ((WmFmNavigationViewDetailsItemBinding)binding).setViewModel(fsoViewModel);
        ((WmFmNavigationViewDetailsItemBinding)binding).setPresenter(presenter);

        setIconByFso(((WmFmNavigationViewDetailsItemBinding)binding).navigationViewItemIcon, fsoViewModel.fso);
    }

}