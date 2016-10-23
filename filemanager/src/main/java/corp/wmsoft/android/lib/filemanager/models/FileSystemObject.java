package corp.wmsoft.android.lib.filemanager.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.io.File;
import java.util.Date;

import corp.wmsoft.android.lib.filemanager.util.FileHelper;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 10:33 AM.<br/>
 * A class that represents an abstract file system object.
 */
@AutoValue
public abstract class FileSystemObject implements Comparable<FileSystemObject> {


    /**
     * @return is fso a directory
     */
    public abstract boolean isDirectory();

    /**
     * @return is fso a ParentDirectory
     */
    public abstract boolean isParentDirectory();

    /**
     * Method that returns the name of the object.
     *
     * @return String The name of the object
     */
    public abstract String name();

    /**
     * Method that returns the parent folder of the object.
     *
     * @return String The parent folder of the object
     */
    public abstract String parent();

    /**
     * Method that returns the size in bytes of the object.
     *
     * @return long The size in bytes of the object
     */
    public abstract long size();

    /**
     * Method that returns the last time that the object was modified.
     *
     * @return Date The last time that the object was modified
     */
    @Nullable
    public abstract Date lastModifiedTime();

    /**
     * Method that returns the full path of the file system object.
     *
     * @return String The full path of the file system object
     */
    @Nullable
    public abstract String fullPath(); //this.mParent + File.separator + this.mName

    /**/
    public abstract int itemsCountInDirectory();

    /**
     * Method that returns if the object is hidden object.
     *
     * @return boolean If the object is hidden object
     */
    public abstract boolean isHidden();

    @Override
    public int compareTo(@NonNull FileSystemObject another) {

        String o1 = this.fullPath();
        if (o1 == null)
            return -1;

        String o2 = another.fullPath();
        if (o2 == null)
            return 1;

        return o1.compareTo(o2);
    }


    private static Builder builder() {
        return new AutoValue_FileSystemObject.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setIsDirectory(boolean isDirectory);
        abstract Builder setIsParentDirectory(boolean isParentDirectory);
        abstract Builder setName(String name);
        abstract Builder setParent(String parent);
        abstract Builder setSize(long size);
        abstract Builder setLastModifiedTime(@Nullable Date lastModifiedTime);
        abstract Builder setFullPath(@Nullable String fullPath);
        abstract Builder setItemsCountInDirectory(int itemsCountInDirectory);
        abstract Builder setIsHidden(boolean isHidden);
        abstract FileSystemObject build();
    }


    // TODO - переделать на билдер
    @NonNull
    public static FileSystemObject createDirectory(String name, String parent, Date lastModifiedTime, int itemsCountInDirectory) {
        return FileSystemObject.builder()

                .setIsDirectory(true)
                .setIsParentDirectory(false)
                .setName(name)
                .setParent(parent)
                .setSize(0)
                .setLastModifiedTime(lastModifiedTime)
                .setFullPath(parent + File.separator + name)
                .setItemsCountInDirectory(itemsCountInDirectory)
                .setIsHidden(name.startsWith("."))
                .build();
    }

    @NonNull
    public static FileSystemObject createParentDirectory(String parent) {
        return FileSystemObject.builder()
                .setIsDirectory(true)
                .setIsParentDirectory(true)
                .setName(FileHelper.PARENT_DIRECTORY)
                .setParent(parent)
                .setSize(0)
                .setLastModifiedTime(null)
                .setFullPath(parent + File.separator + FileHelper.PARENT_DIRECTORY)
                .setItemsCountInDirectory(0)
                .setIsHidden(false)
                .build();
    }

    @NonNull
    public static FileSystemObject createRegularFile(String name, String parent, long size, Date lastModifiedTime) {
        return FileSystemObject.builder()
                .setIsDirectory(false)
                .setIsParentDirectory(false)
                .setName(name)
                .setParent(parent)
                .setSize(size)
                .setLastModifiedTime(lastModifiedTime)
                .setFullPath(parent + File.separator + name)
                .setItemsCountInDirectory(0)
                .setIsHidden(name.startsWith("."))
                .build();
    }

}
