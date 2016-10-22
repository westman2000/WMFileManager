package corp.wmsoft.android.lib.filemanager.models;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Date;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpcrx.viewmodel.IMVPCViewModel;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 10:33 AM.<br/>
 * A class that represents an abstract file system object.
 *
 * @see RegularFile
 * @see Directory
 *
 */
public abstract class FileSystemObject implements IMVPCViewModel, Comparable<FileSystemObject> {

    //Resource identifier for default icon
    private static final int RESOURCE_ICON_DEFAULT = R.drawable.ic_fso_default_24dp;

    /**/
    private int     mResourceIconId;
    private String  mName;
    private String  mParent;
    private long    mSize;
    private Date    mLastModifiedTime;
    private Date    mLastChangedTime;


    /**
     * To prevent always ask
     * <pre>
     *      if (fso instanceof Directory) {
     *          ...
     *      }
     * </pre>
     * @return is fso a directory
     */
    public abstract boolean isDirectory();

    /**
     * To prevent always ask
     * <pre>
     *      if (fso instanceof ParentDirectory) {
     *          ...
     *      }
     * </pre>
     * @return is fso a directory
     */
    public abstract boolean isParentDirectory();

    /**
     * Constructor of <code>FileSystemObject</code>.
     *
     * @param name The name of the object
     * @param parent The parent folder of the object
     * @param size The size in bytes of the object
     * @param lastModifiedTime The last time that the object was modified
     * @param lastChangedTime The last time that the object was changed
     */
    public FileSystemObject(String name, String parent, long size, Date lastModifiedTime, Date lastChangedTime) {
        super();
        this.mName = name;
        this.mParent = parent;
        this.mSize = size;
        this.mLastModifiedTime = lastModifiedTime;
        this.mLastChangedTime = lastChangedTime;
        this.mResourceIconId = RESOURCE_ICON_DEFAULT;
    }

    /**
     * Method that returns the name of the object.
     *
     * @return String The name of the object
     */
    public String getName() {
        return this.mName;
    }

    /**
     * Method that sets the name of the object.
     *
     * @param name The name to set
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Method that returns the parent folder of the object.
     *
     * @return String The parent folder of the object
     */
    public String getParent() {
        return this.mParent;
    }

    /**
     * Method that sets the parent folder of the object.
     *
     * @param parent The parent folder of the object
     */
    public void setParent(String parent) {
        this.mParent = parent;
    }

    /**
     * Method that returns the size in bytes of the object.
     *
     * @return long The size in bytes of the object
     */
    public long getSize() {
        return this.mSize;
    }

    /**
     * Method that sets the size in bytes of the object.
     *
     * @param size The size in bytes of the object
     */
    public void setSize(long size) {
        this.mSize = size;
    }

    /**
     * Method that returns the last time that the object was modified.
     *
     * @return Date The last time that the object was modified
     */
    public Date getLastModifiedTime() {
        return this.mLastModifiedTime;
    }

    /**
     * Method that returns if the object is hidden object.
     *
     * @return boolean If the object is hidden object
     */
    public boolean isHidden() {
        return this.mName.startsWith(".");
    }

    /**
     * Method that sets the identifier of the drawable icon associated
     * to the object.
     *
     * @param resourceIconId The identifier of the drawable icon
     * @hide
     */
    void setResourceIconId(int resourceIconId) {
        this.mResourceIconId = resourceIconId;
    }

    /**
     * Method that returns the full path of the file system object.
     *
     * @return String The full path of the file system object
     */
    public String getFullPath() {
        if (FileHelper.isRootDirectory(this)) {
            return FileHelper.ROOT_DIRECTORY;
        } else if (FileHelper.isParentRootDirectory(this)) {
            if (this.mParent == null) {
                return FileHelper.ROOT_DIRECTORY + this.mName;
            }
            return this.mParent + this.mName;
        }
        return this.mParent + File.separator + this.mName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@NonNull FileSystemObject another) {
        String o1 = this.getFullPath();
        String o2 = another.getFullPath();
        return o1.compareTo(o2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.mName == null) ? 0 : this.mName.hashCode());
        result = prime * result + ((this.mParent == null) ? 0 : this.mParent.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (this == obj)
            return true;
        FileSystemObject other = (FileSystemObject) obj;
        if (this.mName == null) {
            if (other.mName != null)
                return false;
        } else if (!this.mName.equals(other.mName))
            return false;
        if (this.mParent == null) {
            if (other.mParent != null)
                return false;
        } else if (!this.mParent.equals(other.mParent))
            return false;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FileSystemObject [mResourceIconId=" + this.mResourceIconId
                + ", mName=" + this.mName + ", mParent=" + this.mParent
                + ", mSize=" + this.mSize
                + ", mLastModifiedTime=" + this.mLastModifiedTime
                + ", mLastChangedTime=" + this.mLastChangedTime
                + "]";
    }
}
