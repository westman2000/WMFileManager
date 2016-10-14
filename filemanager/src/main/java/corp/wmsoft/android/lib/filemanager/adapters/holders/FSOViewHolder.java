package corp.wmsoft.android.lib.filemanager.adapters.holders;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.IconsHelper;
import corp.wmsoft.android.lib.filemanager.util.MimeTypeHelper;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:27 PM.<br/>
 */
public class FSOViewHolder extends RecyclerView.ViewHolder {

    //The resource of the item icon
    private static final int RESOURCE_ITEM_ICON    = R.id.navigation_view_item_icon;
    //The resource of the item name
    private static final int RESOURCE_ITEM_NAME    = R.id.navigation_view_item_name;
    //The resource of the item summary information
    private static final int RESOURCE_ITEM_SUMMARY = R.id.navigation_view_item_summary;
    //The resource of the item size information
    private static final int RESOURCE_ITEM_SIZE    = R.id.navigation_view_item_size;

    private ImageView mIvIcon;
    private TextView  mTvName;
    private TextView  mTvSummary;
    private TextView  mTvSize;


    public FSOViewHolder(View itemView) {
        super(itemView);
        mIvIcon     = (ImageView)itemView.findViewById(RESOURCE_ITEM_ICON);
        mTvName     = (TextView)itemView.findViewById(RESOURCE_ITEM_NAME);
        mTvSummary  = (TextView)itemView.findViewById(RESOURCE_ITEM_SUMMARY);
        mTvSize     = (TextView)itemView.findViewById(RESOURCE_ITEM_SIZE);
    }

    public void bind(FileSystemObject fso) {

        int iconResId = MimeTypeHelper.getIcon(mIvIcon.getContext(), fso, true);
        Drawable drawable = IconsHelper.getDrawable(iconResId);
        IconsHelper.loadDrawable(mIvIcon, fso, drawable);

        mIvIcon.setAlpha(fso.isHidden() ? 0.3f : 1.0f);

        // was strange bug
        // java.lang.ArrayIndexOutOfBoundsException: length=61; index=-1
        // at android.text.StaticLayout.calculateEllipsis(StaticLayout.java:785)
        mTvName.setText(fso.getName());

        if (mTvSummary != null) {
            if (fso instanceof ParentDirectory) {
                mTvSummary.setText(R.string.wm_fm_parent_dir);
            } else {
                mTvSummary.setText(FileHelper.formatFileTime(mTvSummary.getContext(), fso.getLastModifiedTime()));
            }
        }

        if (mTvSize != null)
            mTvSize.setText(FileHelper.getHumanReadableSize(mTvSize.getContext(), fso));
    }

}
