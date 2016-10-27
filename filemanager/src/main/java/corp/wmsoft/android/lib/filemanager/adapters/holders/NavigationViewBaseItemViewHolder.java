package corp.wmsoft.android.lib.filemanager.adapters.holders;

import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.FixedSizeImageView;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IFileManagerViewContract;
import corp.wmsoft.android.lib.filemanager.util.IconsHelper;
import corp.wmsoft.android.lib.filemanager.util.MimeTypeHelper;


/**
 *
 */
public abstract class NavigationViewBaseItemViewHolder extends RecyclerView.ViewHolder {

    /**/
    /**/
    public final ViewDataBinding binding;


    NavigationViewBaseItemViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public abstract void bind(FSOViewModel fsoViewModel, IFileManagerViewContract.Presenter presenter);

    public static void setIconByFso(FixedSizeImageView imageView, FileSystemObject fso) {
        int iconResId = MimeTypeHelper.getIcon(imageView.getContext(), fso, true);
        Drawable drawable = IconsHelper.getDrawable(iconResId);
        IconsHelper.loadDrawable(imageView, fso, drawable);
        imageView.setAlpha(fso.isHidden() ? 0.3f : 1.0f);
    }

}