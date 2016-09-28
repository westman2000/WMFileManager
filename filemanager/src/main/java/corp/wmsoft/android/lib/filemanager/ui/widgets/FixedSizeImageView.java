package corp.wmsoft.android.lib.filemanager.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * A subclass of ImageView that assumes to be fixed size
 * (not wrap_content / match_parent). Doing so it can
 * optimize the drawable change code paths.
 */
public class FixedSizeImageView extends AppCompatImageView {

    /**/
    private boolean mSuppressLayoutRequest;

    public FixedSizeImageView(Context context) {
        super(context);
        this.setScaleType(ScaleType.FIT_XY); // for fix bug https://code.google.com/p/android/issues/detail?id=202019
    }

    public FixedSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setScaleType(ScaleType.FIT_XY); // for fix bug https://code.google.com/p/android/issues/detail?id=202019
    }

    public FixedSizeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setScaleType(ScaleType.FIT_XY); // for fix bug https://code.google.com/p/android/issues/detail?id=202019
    }

    public void setImageResource(int resId) {
        mSuppressLayoutRequest = true;
        super.setImageResource(resId);
        mSuppressLayoutRequest = false;
    }

    public void setImageURI(Uri uri) {
        mSuppressLayoutRequest = true;
        super.setImageURI(uri);
        mSuppressLayoutRequest = false;
    }

    public void setImageDrawable(Drawable drawable) {
        mSuppressLayoutRequest = true;
        super.setImageDrawable(drawable);
        mSuppressLayoutRequest = false;
    }

    public void requestLayout() {
        if (!mSuppressLayoutRequest) {
            super.requestLayout();
        }
    }
}
