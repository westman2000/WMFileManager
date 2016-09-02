package corp.wmsoft.android.lib.filemanager.models;

import java.util.Date;

/**
 * <br/>Created by WestMan2000 on 8/31/16 at 10:37 AM.<br/>
 * A class that represents a regular file.
 */
public class RegularFile extends FileSystemObject {


    /**
     * Constructor of <code>RegularFile</code>.
     *
     * @param name The name of the object
     * @param parent The parent folder of the object
     * @param size The size in bytes of the object
     * @param lastModifiedTime The last time that the object was modified
     * @param lastChangedTime The last time that the object was changed
     */
    public RegularFile(String name, String parent, long size, Date lastModifiedTime, Date lastChangedTime) {
        super(name, parent, size, lastModifiedTime, lastChangedTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RegularFile [type=" + super.toString() + "]";
    }

}