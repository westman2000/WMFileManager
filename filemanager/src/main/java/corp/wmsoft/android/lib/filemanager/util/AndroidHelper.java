package corp.wmsoft.android.lib.filemanager.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;


/**
 * Useful methods for android
 */
public class AndroidHelper {


    /**
     * Get drawable by res id, needed to support API lover than 21 for vector drawable
     * @param context context
     * @param drawableResId vector drawable id
     * @return drawable
     */
    public static Drawable getVectorDrawable(Context context, @DrawableRes int drawableResId) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            return ContextCompat.getDrawable(context, drawableResId);
        } else {
            return VectorDrawableCompat.create(context.getResources(), drawableResId, context.getTheme());
        }
    }
}
