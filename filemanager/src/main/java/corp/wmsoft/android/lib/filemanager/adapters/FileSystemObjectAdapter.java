package corp.wmsoft.android.lib.filemanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.adapters.holders.FSOViewHolder;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;

/**
 * <br/>Created by WestMan2000 on 9/1/16 at 12:09 PM.<br/>
 */
public class FileSystemObjectAdapter extends RecyclerView.Adapter<FSOViewHolder> {

    /**/
    @SuppressWarnings("unused")
    private final static String TAG = "FileManager";

    /**/
    private List<FileSystemObject> mFileSystemObjects;
    /**/
    private View.OnClickListener   mViewOnClickListener;
    /**/
    @IFileManagerNavigationMode
    private int mCurrentNavigationMode;


    public FileSystemObjectAdapter() {
        this.mFileSystemObjects = new ArrayList<>();
        mCurrentNavigationMode = IFileManagerNavigationMode.DETAILS;
    }

    @Override
    public FSOViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        FSOViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v;

        switch (viewType) {
            case IFileManagerNavigationMode.ICONS:
                v = inflater.inflate(R.layout.navigation_view_icons_item, parent, false);
                break;
            case IFileManagerNavigationMode.SIMPLE:
                v = inflater.inflate(R.layout.navigation_view_simple_item, parent, false);
                break;
            default:
                v = inflater.inflate(R.layout.navigation_view_details_item, parent, false);
                break;
        }

        if (mViewOnClickListener != null)
            v.setOnClickListener(mViewOnClickListener);

        viewHolder = new FSOViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FSOViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @IFileManagerNavigationMode
    @Override
    public int getItemViewType(int position) {
        return this.mCurrentNavigationMode;
    }

    @Override
    public int getItemCount() {
        return mFileSystemObjects.size();
    }

    public void setNavigationMode(@IFileManagerNavigationMode int newMode) {
        //Check that it is really necessary change the mode
        if (this.mCurrentNavigationMode == newMode) return;

        this.mCurrentNavigationMode = newMode;
        notifyDataSetChanged();
    }

    public FileSystemObject getItem(int position) {
        return this.mFileSystemObjects.get(position);
    }

    public void clearAndAddAll(List<FileSystemObject> viewModels) {
        this.mFileSystemObjects.clear();
        this.mFileSystemObjects.addAll(viewModels);
        notifyDataSetChanged();
    }

    public void setViewOnClickListener(View.OnClickListener viewOnClickListener) {
        this.mViewOnClickListener = viewOnClickListener;
    }
}
