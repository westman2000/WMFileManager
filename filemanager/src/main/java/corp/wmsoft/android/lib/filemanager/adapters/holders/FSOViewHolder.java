package corp.wmsoft.android.lib.filemanager.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:27 PM.<br/>
 */
public class FSOViewHolder extends RecyclerView.ViewHolder {

    //The resource of the item icon
    protected static final int RESOURCE_ITEM_ICON    = R.id.navigation_view_item_icon;
    //The resource of the item name
    protected static final int RESOURCE_ITEM_NAME    = R.id.navigation_view_item_name;
    //The resource of the item summary information
    protected static final int RESOURCE_ITEM_SUMMARY = R.id.navigation_view_item_summary;
    //The resource of the item size information
    protected static final int RESOURCE_ITEM_SIZE    = R.id.navigation_view_item_size;

    protected ImageView mIvIcon;
    protected TextView  mTvName;
    protected TextView  mTvSummary;
    protected TextView  mTvSize;


    public FSOViewHolder(View itemView) {
        super(itemView);
        mIvIcon     = (ImageView)itemView.findViewById(RESOURCE_ITEM_ICON);
        mTvName     = (TextView)itemView.findViewById(RESOURCE_ITEM_NAME);
        mTvSummary  = (TextView)itemView.findViewById(RESOURCE_ITEM_SUMMARY);
        mTvSize     = (TextView)itemView.findViewById(RESOURCE_ITEM_SIZE);
    }

    public void bind(FileSystemObject fso) {

        mIvIcon.setImageResource(fso.getResourceIconId());

        mTvName.setText(fso.getName());

        if (mTvSummary != null) {
            if (fso instanceof ParentDirectory) {
                mTvSummary.setText(R.string.parent_dir);
            } else {
                mTvSummary.setText(FileHelper.formatFileTime(mTvSummary.getContext(), fso.getLastModifiedTime()));
            }
        }

        if (mTvSize != null)
            mTvSize.setText(FileHelper.getHumanReadableSize(mTvSize.getContext(), fso));
    }

}
