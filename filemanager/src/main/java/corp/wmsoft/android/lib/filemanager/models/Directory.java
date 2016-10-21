package corp.wmsoft.android.lib.filemanager.models;

import java.util.Date;

import corp.wmsoft.android.lib.filemanager.R;


/**
 * A class that represents a directory.
 *
 * <br/>Created by WestMan2000 on 8/31/16 at 10:33 AM.<br/>
 */
public class Directory extends FileSystemObject {

    //Resource identifier for default icon
    private static final int RESOURCE_FOLDER_DEFAULT = R.drawable.ic_fso_folder_24dp;

    /**/
    private final int itemsCountInDirectory;

    /**
     * Constructor of <code>Directory</code>.
     *
     * @param name The name of the object
     * @param parent The parent folder of the object
     * @param lastModifiedTime The last time that the object was modified
     * @param lastChangedTime The last time that the object was changed
     */
    public Directory(String name, String parent, Date lastModifiedTime, Date lastChangedTime, int itemsCount) {
        super(name, parent, 0L, lastModifiedTime, lastChangedTime);
        itemsCountInDirectory = itemsCount;
        setResourceIconId(RESOURCE_FOLDER_DEFAULT);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isParentDirectory() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Directory [type=" + super.toString() + "]";
    }

    public int getItemsCountInDirectory() {
        return itemsCountInDirectory;
    }
}