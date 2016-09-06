package corp.wmsoft.android.lib.filemanager.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
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

}
