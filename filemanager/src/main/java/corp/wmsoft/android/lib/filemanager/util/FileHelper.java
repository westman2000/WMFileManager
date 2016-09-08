package corp.wmsoft.android.lib.filemanager.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.models.RegularFile;


/**
 * A helper class with useful methods for deal with files.
 */
public class FileHelper {

    /**
     * The root directory.
     * @hide
     */
    public static final String ROOT_DIRECTORY = "/";

    /**
     * The parent directory string.
     * @hide
     */
    public static final String PARENT_DIRECTORY = "..";

    /**
     * @hide
     */
    public static boolean sReloadDateTimeFormats = true;
    private static String sDateTimeFormatOrder = null;
    private static @IFileManagerFileTimeFormat int sFileTimeFormat;
    private static DateFormat sDateFormat = null;
    private static DateFormat sTimeFormat = null;


    /**
     * Method that creates a {@link FileSystemObject} from a {@link File}
     *
     * @param file The file or folder reference
     * @return FileSystemObject The file system object reference
     */
    public static FileSystemObject createFileSystemObject(File file) {

        // Build a directory?
        Date lastModified = new Date(file.lastModified());
        if (file.isDirectory()) {
            return
                    new Directory(
                            file.getName(),
                            file.getParent(),
                            lastModified, lastModified); // The only date we have
        }

        // Build a regular file
        return
                new RegularFile(
                        file.getName(),
                        file.getParent(),
                        file.length(),
                        lastModified, lastModified); // The only date we have

    }

    /**
     * Method that returns a more human readable of the size
     * of a file system object.
     *
     * @param fso File system object
     * @return String The human readable size (void if fso don't supports size)
     */
    public static String getHumanReadableSize(Context context, FileSystemObject fso) {
        //Only if has size
        if (fso instanceof Directory) {
            return "";
        }
        return getHumanReadableSize(context, fso.getSize());
    }

    /**
     * Method that returns a more human readable of a size in bytes.
     *
     * @param size The size in bytes
     * @return String The human readable size
     */
    public static String getHumanReadableSize(Context context, long size) {
        Resources res = context.getResources();
        final int[] magnitude = {
                R.string.size_bytes,
                R.string.size_kilobytes,
                R.string.size_megabytes,
                R.string.size_gigabytes
        };

        double aux = size;
        for (int aMagnitude : magnitude) {
            if (aux < 1024) {
                double cleanSize = Math.round(aux * 100);
                return Double.toString(cleanSize / 100) + " " + res.getString(aMagnitude);
            } else {
                aux = aux / 1024;
            }
        }
        double cleanSize = Math.round(aux * 100);
        return Double.toString(cleanSize / 100) + " " + res.getString(magnitude[magnitude.length - 1]);
    }

    /**
     * Method that returns if the file system object if the root directory.
     *
     * @param fso The file system object to check
     * @return boolean if the file system object if the root directory
     */
    public static boolean isRootDirectory(FileSystemObject fso) {
        return fso.getName() == null || fso.getName().compareTo(FileHelper.ROOT_DIRECTORY) == 0;
    }

    /**
     * Method that returns if the parent file system object if the root directory.
     *
     * @param fso The parent file system object to check
     * @return boolean if the parent file system object if the root directory
     */
    public static boolean isParentRootDirectory(FileSystemObject fso) {
        return fso.getParent() == null || fso.getParent().compareTo(FileHelper.ROOT_DIRECTORY) == 0;
    }

    /**
     * Method that formats a filetime date with the specific system settings
     *
     * @param ctx The current context
     * @param filetime The filetime date
     * @return String The filetime date formatted
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatFileTime(Context ctx, Date filetime) {
        if (sReloadDateTimeFormats) {

            sFileTimeFormat = PreferencesHelper.getFileManagerFileTimeFormat();

            if (sFileTimeFormat == IFileManagerFileTimeFormat.SYSTEM) {
                sDateTimeFormatOrder = ctx.getString(R.string.datetime_format_order);
                sDateFormat = android.text.format.DateFormat.getDateFormat(ctx);
                sTimeFormat = android.text.format.DateFormat.getTimeFormat(ctx);
            } else if (sFileTimeFormat == IFileManagerFileTimeFormat.LOCALE) {
                sDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            } else if (sFileTimeFormat == IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS) {
                sDateFormat = new SimpleDateFormat(ctx.getString(R.string.filetime_format_mode_ddMMyyyy_HHmmss));
            } else if (sFileTimeFormat == IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS) {
                sDateFormat = new SimpleDateFormat(ctx.getString(R.string.filetime_format_mode_MMddyyyy_HHmmss));
            } else if (sFileTimeFormat == IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS) {
                sDateFormat = new SimpleDateFormat(ctx.getString(R.string.filetime_format_mode_yyyyMMdd_HHmmss));
            }
            sReloadDateTimeFormats = false;
        }

        // Apply the user settings
        if (sFileTimeFormat == IFileManagerFileTimeFormat.SYSTEM) {
            String date = sDateFormat.format(filetime);
            String time = sTimeFormat.format(filetime);
            return String.format(sDateTimeFormatOrder, date, time);
        } else {
            return sDateFormat.format(filetime);
        }
    }

    /**
     * Method that applies the configuration modes to the listed files
     * (sort mode, hidden files, ...).
     *
     * @param files The listed files
     * @return List<FileSystemObject> The applied mode listed files
     */
    public static List<FileSystemObject> applyUserPreferences(List<FileSystemObject> files) {

        //Retrieve user preferences
        final boolean showDirsFirst = PreferencesHelper.isShowDirsFirst();
        final boolean showHidden = PreferencesHelper.isShowHidden();
        final @IFileManagerSortMode int sortMode = PreferencesHelper.getFileManagerSortMode();

        //Remove all unnecessary files (no required by the user)
        int cc = files.size();
        for (int i = cc - 1; i >= 0; i--) {
            FileSystemObject file = files.get(i);

            //Hidden files
            if (!showHidden) {
                if (file.isHidden()) {
                    files.remove(i);
                }
            }
            // TODO -  Restrictions (only apply to files)
        }

        //Apply sort mode
        Collections.sort(files, new Comparator<FileSystemObject>() {
            @Override
            public int compare(FileSystemObject lhs, FileSystemObject rhs) {
                //Parent directory always goes first
                boolean isLhsParentDirectory = lhs instanceof ParentDirectory;
                boolean isRhsParentDirectory = rhs instanceof ParentDirectory;
                if (isLhsParentDirectory || isRhsParentDirectory) {
                    if (isLhsParentDirectory && isRhsParentDirectory) {
                        return 0;
                    }
                    return (isLhsParentDirectory) ? -1 : 1;
                }

                //Need to sort directory first?
                if (showDirsFirst) {
                    boolean isLhsDirectory = lhs instanceof Directory;
                    boolean isRhsDirectory = rhs instanceof Directory;
                    if (isLhsDirectory || isRhsDirectory) {
                        if (isLhsDirectory && isRhsDirectory) {
                            //Apply sort mode
                            return FileHelper.doCompare(lhs, rhs, sortMode);
                        }
                        return (isLhsDirectory) ? -1 : 1;
                    }
                }

                //Apply sort mode
                return FileHelper.doCompare(lhs, rhs, sortMode);
            }

        });

        //Return the files
        return files;
    }

    /**
     * Method that do a comparison between 2 file system objects.
     *
     * @param fso1 The first file system objects
     * @param fso2 The second file system objects
     * @param mode The sort mode
     * @return int a negative integer if {@code fso1} is less than {@code fso2};
     *         a positive integer if {@code fso1} is greater than {@code fso2};
     *         0 if {@code fso1} has the same order as {@code fso2}.
     */
    public static int doCompare(
            final FileSystemObject fso1,
            final FileSystemObject fso2,
            final @IFileManagerSortMode int mode) {

        // Retrieve the user preference for case sensitive sort
        boolean caseSensitive = PreferencesHelper.isCaseSensitiveSort();

        //Name (ascending)
        if (mode == IFileManagerSortMode.NAME_ASC) {
            if (!caseSensitive) {
                return fso1.getName().compareToIgnoreCase(fso2.getName());
            }
            return fso1.getName().compareTo(fso2.getName());
        }
        //Name (descending)
        if (mode == IFileManagerSortMode.NAME_DESC) {
            if (!caseSensitive) {
                return fso1.getName().compareToIgnoreCase(fso2.getName()) * -1;
            }
            return fso1.getName().compareTo(fso2.getName()) * -1;
        }

        //Date (ascending)
        if (mode == IFileManagerSortMode.DATE_ASC) {
            return fso1.getLastModifiedTime().compareTo(fso2.getLastModifiedTime());
        }
        //Date (descending)
        if (mode == IFileManagerSortMode.DATE_DESC) {
            return fso1.getLastModifiedTime().compareTo(fso2.getLastModifiedTime()) * -1;
        }

        //Size (ascending)
        if (mode == IFileManagerSortMode.SIZE_ASC) {
            return Long.valueOf(fso1.getSize()).compareTo(fso2.getSize());
        }
        //Size (descending)
        if (mode == IFileManagerSortMode.SIZE_DESC) {
            return Long.valueOf(fso1.getSize()).compareTo(fso2.getSize()) * -1;
        }

//        //Type (ascending)
//        if (mode == IFileManagerSortMode.TYPE_ASC) {
//            // Shouldn't need context here, mimetypes should be loaded
//            return MimeTypeHelper.compareFSO(null, fso1, fso2);
//        }
//        //Type (descending)
//        if (mode == IFileManagerSortMode.TYPE_DESC) {
//            // Shouldn't need context here, mimetypes should be loaded
//            return MimeTypeHelper.compareFSO(null, fso1, fso2) * -1;
//        }

        //Comparison between files directly
        return fso1.compareTo(fso2);
    }
}
