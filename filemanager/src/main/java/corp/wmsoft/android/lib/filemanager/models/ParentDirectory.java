package corp.wmsoft.android.lib.filemanager.models;

import corp.wmsoft.android.lib.filemanager.util.FileHelper;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 10:36 AM.<br/>
 * A class that represents a link to the parent directory.
 */
public class ParentDirectory extends Directory {

    /**
     * Constructor of <code>ParentDirectory</code>.
     *
     * @param parent The parent folder of the object
     */
    public ParentDirectory(String parent) {
        super(FileHelper.PARENT_DIRECTORY, parent, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isParentDirectory() {
        return true;
    }
}
