package corp.wmsoft.android.lib.filemanager.adapters.holders;

import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewSimpleItemBinding;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 *
 */
public class NavigationViewSimpleItemViewHolder extends NavigationViewBaseItemViewHolder {


    public NavigationViewSimpleItemViewHolder(WmFmNavigationViewSimpleItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(FSOViewModel fsoViewModel, IFileManagerViewContract.Presenter presenter) {
        ((WmFmNavigationViewSimpleItemBinding)binding).setViewModel(fsoViewModel);
        ((WmFmNavigationViewSimpleItemBinding)binding).setPresenter(presenter);

        setIconByFso(((WmFmNavigationViewSimpleItemBinding)binding).navigationViewItemIcon, fsoViewModel.fso);
    }

}