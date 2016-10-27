package corp.wmsoft.android.lib.filemanager.adapters.holders;

import corp.wmsoft.android.lib.filemanager.databinding.WmFmNavigationViewIconsItemBinding;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;


/**
 *
 */
public class NavigationViewIconsItemViewHolder extends NavigationViewBaseItemViewHolder {


    public NavigationViewIconsItemViewHolder(WmFmNavigationViewIconsItemBinding binding) {
        super(binding);
    }

    @Override
    public void bind(FSOViewModel fsoViewModel, IFileManagerViewContract.Presenter presenter) {
        ((WmFmNavigationViewIconsItemBinding)binding).setViewModel(fsoViewModel);
        ((WmFmNavigationViewIconsItemBinding)binding).setPresenter(presenter);

        setIconByFso(((WmFmNavigationViewIconsItemBinding)binding).navigationViewItemIcon, fsoViewModel.fso);
    }

}