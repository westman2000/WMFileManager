package corp.wmsoft.android.lib.filemanager.util;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.FixedSizeImageView;


/**
 * Created by admin on 10/26/16.
 */
@Keep
public class BindingHelper {

    @Keep
    @BindingAdapter("iconByFso")
    public static void setIconByFso(FixedSizeImageView imageView, FileSystemObject fso) {
        int iconResId = MimeTypeHelper.getIcon(imageView.getContext(), fso, true);
        Drawable drawable = IconsHelper.getDrawable(iconResId);
        IconsHelper.loadDrawable(imageView, fso, drawable);
        imageView.setAlpha(fso.isHidden() ? 0.3f : 1.0f);
    }
}
