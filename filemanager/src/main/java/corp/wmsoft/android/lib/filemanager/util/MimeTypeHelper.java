package corp.wmsoft.android.lib.filemanager.util;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.models.Directory;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;

/**
 * A helper class with useful methods for deal with mime types.
 */
@SuppressWarnings({"WeakerAccess", "unused", "FinalStaticMethod", "Convert2Diamond", "FinalPrivateMethod"})
public final class MimeTypeHelper {

    /**
     * Enumeration of mime/type' categories
     */

    public enum MimeTypeCategory {
        /**
         * No category
         */
        NONE,
        /**
         * System file
         */
        SYSTEM,
        /**
         * Application, Installer, ...
         */
        APP,
        /**
         * Binary file
         */
        BINARY,
        /**
         * Text file
         */
        TEXT,
        /**
         * Document file (text, spreedsheet, presentation, pdf, ...)
         */
        DOCUMENT,
        /**
         * e-Book file
         */
        EBOOK,
        /**
         * Mail file (email, message, contact, calendar, ...)
         */
        MAIL,
        /**
         * Compressed file
         */
        COMPRESS,
        /**
         * Executable file
         */
        EXEC,
        /**
         * Database file
         */
        DATABASE,
        /**
         * Font file
         */
        FONT,
        /**
         * Image file
         */
        IMAGE,
        /**
         * Audio file
         */
        AUDIO,
        /**
         * Video file
         */
        VIDEO,
        /**
         * Security file (certificate, keys, ...)
         */
        SECURITY;

        public static String[] names() {
            MimeTypeCategory[] categories = values();
            String[] names = new String[categories.length];

            for (int i = 0; i < categories.length; i++) {
                names[i] = categories[i].name();
            }

            return names;
        }

        public static String[] getFriendlyLocalizedNames(Context context) {
            MimeTypeCategory[] categories = values();
            String[] localizedNames = new String[categories.length];

            for (int i = 0; i < categories.length; i++) {
                String description = getCategoryDescription(context, categories[i]);
                if (TextUtils.equals("-", description)) {
                    description = context.getString(R.string.category_all);
                }
                description = description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
                localizedNames[i] = description;
            }

            return localizedNames;
        }
    }

    /**
     * An internal class for holding the mime/type database structure
     */
    private static class MimeTypeInfo {

        public MimeTypeCategory mCategory;
        public String mMimeType;
        public int mDrawableId;

        MimeTypeInfo() {/**NON BLOCK**/}

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((this.mCategory == null) ? 0 : this.mCategory.hashCode());
            result = prime * result + mDrawableId;
            result = prime * result
                    + ((this.mMimeType == null) ? 0 : this.mMimeType.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MimeTypeInfo other = (MimeTypeInfo) obj;
            if (this.mCategory != other.mCategory)
                return false;
            if (this.mMimeType == null) {
                if (other.mMimeType != null)
                    return false;
            } else if (!this.mMimeType.equals(other.mMimeType))
                return false;

            return this.mDrawableId == other.mDrawableId;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "MimeTypeInfo [mCategory=" + this.mCategory +
                    ", mMimeType="+ this.mMimeType +
                    ", mDrawableId=" + this.mDrawableId + "]";
        }
    }

    private static final String TAG = "MimeTypeHelper"; //$NON-NLS-1$

    /**
     * A constant that defines a string of all mime-types
     */
    public static final String ALL_MIME_TYPES = "*/*"; //$NON-NLS-1$

    private static Map<String, ArrayList<MimeTypeInfo>> sMimeTypes;
    /**
     * Maps from a combination key of <extension> + <mimetype> to MimeTypeInfo objects.
     */
    private static HashMap<String, MimeTypeInfo> sExtensionMimeTypes;

    /**
     * Constructor of <code>MimeTypeHelper</code>.
     */
    private MimeTypeHelper() {
        super();
    }

    /**
      * Method that checks whether it is a special android cursor mimetype
      * that we don't support and should filter it all
      *
      * @param mimeType The mime type to be checked
      * @return true if mime type is known, false otherwise
      */
     public static final boolean isAndroidCursorMimeType(String mimeType) {
         return mimeType.startsWith("vnd.android.cursor");
     }

    /**
     * Method that checks whether a certain mime type is known to
     * the application.
     *
     * @param context The current context
     * @param mimeType The mime type to be checked
     * @return true if mime type is known, false otherwise
     */
    public static final boolean isMimeTypeKnown(Context context, String mimeType) {
        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        if (mimeType == null) {
            return false;
        }

        for (ArrayList<MimeTypeInfo> mimeTypeInfoList : sMimeTypes.values()) {
            for (MimeTypeInfo info : mimeTypeInfoList) {
                String mimeTypeRegExp = convertToRegExp(mimeType);
                if (info.mMimeType.matches(mimeTypeRegExp)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method that returns the associated mime/type icon resource identifier of
     * the {@link FileSystemObject}.
     *
     * @param context The current context
     * @param fso The file system object
     * @return String The associated mime/type icon resource identifier
     */
    public static final @DrawableRes int getIcon(Context context, FileSystemObject fso) {
        return getIcon(context, fso, false);
    }

    public static final @DrawableRes int getIcon(Context context, FileSystemObject fso, boolean firstFound) {

        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        //Check if the argument is a folder
        if (fso instanceof Directory) {
            return R.drawable.ic_fso_folder_24dp;
        }

        //Get the extension and delivery
        String ext = FileHelper.getExtension(fso);
        if (ext != null) {
            MimeTypeInfo mimeTypeInfo = getMimeTypeInternal(fso, ext, firstFound);

            if (mimeTypeInfo != null) {
                // Create a new drawable
                if (mimeTypeInfo.mDrawableId > 0) {
                    return mimeTypeInfo.mDrawableId;
                }

                // Something was wrong here. The resource should exist, but it's not present.
                // Audit the wrong mime/type resource and return the best fso drawable (probably
                // default)
                Log.w(TAG, String.format(
                        "Something was wrong with the drawable of the fso:" + //$NON-NLS-1$
                        "%s, mime: %s", //$NON-NLS-1$
                        fso.toString(),
                        mimeTypeInfo.toString()));
            }
        }

        return R.drawable.ic_fso_default_24dp;
    }

    /**
     * Method that returns the mime/type of the {@link FileSystemObject}.
     *
     * @param context The current context
     * @param fso The file system object
     * @return String The mime/type
     */
    public static final String getMimeType(Context context, FileSystemObject fso) {
        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        //Directories don't have a mime type
        if (FileHelper.isDirectory(fso)) {
            return null;
        }

        //Get the extension and delivery
        return getMimeTypeFromExtension(fso);
    }

    /**
     * Method that compares {@link FileSystemObject} by MimeTypeCategory
     *
     * @param context The current context
     * @param fso1 File system object 1
     * @param fso2 File system object 2
     * @return int Either -1, 0, 1 based on if fso1 appears before or after fso2
     */
    public static final int compareFSO(Context context, FileSystemObject fso1, FileSystemObject fso2) {
        MimeTypeCategory mtc1 = getCategory(context, fso1);
        MimeTypeCategory mtc2 = getCategory(context, fso2);

        return mtc1.compareTo(mtc2);
    }

    /**
     * Method that returns the mime/type description of the {@link FileSystemObject}.
     *
     * @param context The current context
     * @param fso The file system object
     * @return String The mime/type description
     */
    public static final String getMimeTypeDescription(Context context, FileSystemObject fso) {
        Resources res = context.getResources();

        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        //Check if the argument is a folder
        if (fso instanceof Directory) {
            return res.getString(R.string.mime_folder);
        }

        //Get the extension and delivery
        String mime = getMimeTypeFromExtension(fso);
        if (mime != null) {
            return mime;
        }

        return res.getString(R.string.mime_unknown);
    }

    /**
     * Gets the mimetype of a file, if there are multiple possibilities given it's extension.
     * @param absolutePath The absolute path of the file for which to find the mimetype.
     * @param ext The extension of the file.
     * @return The correct mimetype for this file, or null if the mimetype cannot be determined
     * or is not ambiguous.
     */
    private static final String getAmbiguousExtensionMimeType(String absolutePath, String ext) {
        if (AmbiguousExtensionHelper.AMBIGUOUS_EXTENSIONS_MAP.containsKey(ext)) {
            AmbiguousExtensionHelper helper =
                    AmbiguousExtensionHelper.AMBIGUOUS_EXTENSIONS_MAP.get(ext);
            String mimeType = helper.getMimeType(absolutePath, ext);
            if (!TextUtils.isEmpty(mimeType)) {
                return mimeType;
            }
        }
        return null;
    }

    /**
     * Get the MimeTypeInfo that describes this file.
     * @param fso The file.
     * @param ext The extension of the file.
     * @return The MimeTypeInfo object that describes this file, or null if it cannot be retrieved.
     */
    private static final MimeTypeInfo getMimeTypeInternal(FileSystemObject fso, String ext) {
        return getMimeTypeInternal(fso.getFullPath(), ext);
    }

    private static final MimeTypeInfo getMimeTypeInternal(FileSystemObject fso,
                                                          String ext,
                                                          boolean firstFound) {
        return getMimeTypeInternal(fso.getFullPath(), ext, firstFound);
    }

    /**
     * Get the MimeTypeInfo that describes this file.
     * @param absolutePath The absolute path of the file.
     * @param ext The extension of the file.
     * @return The MimeTypeInfo object that describes this file, or null if it cannot be retrieved.
     */
    private static final MimeTypeInfo getMimeTypeInternal(String absolutePath, String ext) {
        return getMimeTypeInternal(absolutePath, ext, false);
    }

    private static final MimeTypeInfo getMimeTypeInternal(String absolutePath,
                                                          String ext,
                                                          boolean firstFound) {
        MimeTypeInfo mimeTypeInfo = null;
        ArrayList<MimeTypeInfo> mimeTypeInfoList = sMimeTypes.get(ext.toLowerCase(Locale.ROOT));
        // Multiple mimetypes map to the same extension, try to resolve it.
        if (mimeTypeInfoList != null && mimeTypeInfoList.size() > 1) {
            if ((absolutePath != null) && (!firstFound)) {
                String mimeType = getAmbiguousExtensionMimeType(absolutePath, ext);
                mimeTypeInfo = sExtensionMimeTypes.get(ext + mimeType);
            } else {
                // We don't have the ability to read the file to resolve the ambiguity,
                // so default to the first available mimetype.
                mimeTypeInfo = mimeTypeInfoList.get(0);
            }
        } else if (mimeTypeInfoList != null && mimeTypeInfoList.size() == 1) {
            // Only one possible mimetype, so pick that one.
            mimeTypeInfo = mimeTypeInfoList.get(0);
        }
        return mimeTypeInfo;
    }

    private static final String getMimeTypeFromExtension(final FileSystemObject fso) {
        String ext = FileHelper.getExtension(fso);
        if (ext == null) {
            return "application/octet-stream";
        }

        // If this extension is ambiguous, attempt to resolve it.
        String mimeType = getAmbiguousExtensionMimeType(fso.getFullPath(), ext);
        if (mimeType != null) {
            return mimeType;
        }

        //Load from the database of mime types
        MimeTypeInfo mimeTypeInfo = getMimeTypeInternal(fso, ext);
        if (mimeTypeInfo == null) {
            return "application/octet-stream";
        }

        return mimeTypeInfo.mMimeType;
    }

    /**
     * Method that returns the mime/type category of the file.
     *
     * @param context The current context
     * @param ext The extension of the file
     * @param absolutePath The absolute path of the file. Can be null if not available.
     * @return MimeTypeCategory The mime/type category
     */
    public static final MimeTypeCategory getCategoryFromExt(Context context, String ext,
                                                            String absolutePath) {
        // Ensure that have a context
        if (context == null && sMimeTypes == null) {
            // No category
            return MimeTypeCategory.NONE;
        }
        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }
        if (ext != null) {
            //Load from the database of mime types
            MimeTypeInfo mimeTypeInfo = getMimeTypeInternal(absolutePath, ext);
            if (mimeTypeInfo != null) {
                return mimeTypeInfo.mCategory;
            }
        }

        // No category
        return MimeTypeCategory.NONE;
    }

    /**
     * Method that returns the mime/type category of the file.
     *
     * @param context The current context
     * @param file The file
     * @return MimeTypeCategory The mime/type category
     */
    public static final MimeTypeCategory getCategory(Context context, File file) {
        // Ensure that have a context
        if (context == null && sMimeTypes == null) {
            // No category
            return MimeTypeCategory.NONE;
        }
        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        // Directory and Symlinks no computes as category
        if (file.isDirectory()) {
            return MimeTypeCategory.NONE;
        }

        //Get the extension and delivery
        return getCategoryFromExt(context,
                                  FileHelper.getExtension(file.getName()),
                                  file.getAbsolutePath());
    }

    /**
     * Method that returns the mime/type category of the file system object.
     *
     * @param context The current context
     * @param fso The file system object
     * @return MimeTypeCategory The mime/type category
     */
    public static final MimeTypeCategory getCategory(Context context, FileSystemObject fso) {
        // Ensure that have a context
        if (context == null && sMimeTypes == null) {
            // No category
            return MimeTypeCategory.NONE;
        }
        //Ensure that mime types are loaded
        if (sMimeTypes == null) {
            loadMimeTypes(context);
        }

        // Directory and Symlinks no computes as category
        if (FileHelper.isDirectory(fso)) {
            return MimeTypeCategory.NONE;
        }

        //Get the extension and delivery
        final MimeTypeCategory category = getCategoryFromExt(context,
                FileHelper.getExtension(fso), fso.getFullPath());

        // Check  system file
        if (category == MimeTypeCategory.NONE) {
            return MimeTypeCategory.SYSTEM;
        }

        return category;
    }

    /**
     * Method that returns the description of the category
     *
     * @param context The current context
     * @param category The category
     * @return String The description of the category
     */
    public static final String getCategoryDescription(
                    Context context, MimeTypeCategory category) {
        if (category == null || category.compareTo(MimeTypeCategory.NONE) == 0) {
            return "-";
        }
        try {
            String id = "category_" + category.toString().toLowerCase(Locale.ROOT); //$NON-NLS-1$
            int resId = context.getResources().getIdentifier(id, "string", context.getPackageName());
            return context.getString(resId);
        } catch (Throwable e) {/**NON BLOCK**/}
        return "-";
    }

    /**
     * Method that returns if a file system object matches with a mime-type expression.
     *
     * @param ctx The current context
     * @param fso The file system object to check
     * @param mimeTypeExpression The mime-type expression (xe: *&#47;*, audio&#47;*)
     * @return boolean If the file system object matches the mime-type expression
     */
    public static final boolean matchesMimeType(
            Context ctx, FileSystemObject fso, String mimeTypeExpression) {
        String mimeType = getMimeType(ctx, fso);
        return mimeType != null && mimeType.matches(convertToRegExp(mimeTypeExpression));
    }

    /**
     * Method that loads the mime type information.
     *
     * @param context The current context
     */
    //IMP! This must be invoked from the main activity creation
    public static synchronized void loadMimeTypes(Context context) {
        if (sMimeTypes == null) {
            try {
                // Load the mime/type database
                Properties mimeTypes = new Properties();
                mimeTypes.load(context.getResources().openRawResource(R.raw.mime_types));

                // Parse the properties to an in-memory structure
                // Format:  <extension> = <category> | <mime type> | <drawable>
                sMimeTypes = new HashMap<String, ArrayList<MimeTypeInfo>>();
                sExtensionMimeTypes = new HashMap<String, MimeTypeInfo>();
                Enumeration<Object> e = mimeTypes.keys();
                while (e.hasMoreElements()) {
                    try {
                        String extension = (String)e.nextElement();
                        String data = mimeTypes.getProperty(extension);
                        String[] datas = data.split(",");
                        for (String theData : datas) {
                            String[] mimeData = theData.split("\\|");

                            // Create a reference of MimeType
                            MimeTypeInfo mimeTypeInfo = new MimeTypeInfo();
                            mimeTypeInfo.mCategory    = MimeTypeCategory.valueOf(mimeData[0].trim());
                            mimeTypeInfo.mMimeType    = mimeData[1].trim();
                            mimeTypeInfo.mDrawableId  = context.getResources().getIdentifier(mimeData[2].trim(), "drawable", context.getPackageName());

                            // If no list exists yet for this mimetype, create one.
                            // Else, add it to the existing list.
                            if (sMimeTypes.get(extension) == null) {
                                ArrayList<MimeTypeInfo> infoList = new ArrayList<MimeTypeInfo>();
                                infoList.add(mimeTypeInfo);
                                sMimeTypes.put(extension, infoList);
                            } else {
                                sMimeTypes.get(extension).add(mimeTypeInfo);
                            }
                            sExtensionMimeTypes.put(extension + mimeTypeInfo.mMimeType,
                                                    mimeTypeInfo);
                        }

                    } catch (Exception e2) { /**NON BLOCK**/}
                }

            } catch (Exception e) {
                Log.e(TAG, "Fail to load mime types raw file.", e); //$NON-NLS-1$
            }
        }
    }

    /**
     * Method that converts the mime-type expression to a regular expression
     *
     * @param mimeTypeExpression The mime-type expression
     * @return String The regular expression
     */
    private static String convertToRegExp(String mimeTypeExpression) {
        return mimeTypeExpression.replaceAll("\\*", ".\\*"); //$NON-NLS-1$ //$NON-NLS-2$
    }


    /**
     * Class for resolve known mime types
     */
    public static final class KnownMimeTypeResolver {

        private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

        /**
         * Method that returns if the FileSystemObject is an Android app.
         *
         * @param context The current context
         * @param fso The FileSystemObject to check
         * @return boolean If the FileSystemObject is an Android app.
         */
        public static boolean isAndroidApp(Context context, FileSystemObject fso) {
            return MIME_TYPE_APK.equals(MimeTypeHelper.getMimeType(context, fso));
        }

        /**
         * Method that returns if the FileSystemObject is an image file.
         *
         * @param context The current context
         * @param fso The FileSystemObject to check
         * @return boolean If the FileSystemObject is an image file.
         */
        public static boolean isImage(Context context, FileSystemObject fso) {
            return MimeTypeHelper.getCategory(context, fso).compareTo(MimeTypeCategory.IMAGE) == 0;
        }

        /**
         * Method that returns if the FileSystemObject is an video file.
         *
         * @param context The current context
         * @param fso The FileSystemObject to check
         * @return boolean If the FileSystemObject is an video file.
         */
        public static boolean isVideo(Context context, FileSystemObject fso) {
            return MimeTypeHelper.getCategory(context, fso).compareTo(MimeTypeCategory.VIDEO) == 0;
        }

        /**
         * Method that returns if the File is an image file.
         *
         * @param context The current context
         * @param file The File to check
         * @return boolean If the File is an image file.
         */
        public static boolean isImage(Context context, File file) {
            return MimeTypeHelper.getCategory(context, file).compareTo(MimeTypeCategory.IMAGE) == 0;
        }

        /**
         * Method that returns if the File is an video file.
         *
         * @param context The current context
         * @param file The File to check
         * @return boolean If the File is an video file.
         */
        public static boolean isVideo(Context context, File file) {
            return MimeTypeHelper.getCategory(context, file).compareTo(MimeTypeCategory.VIDEO) == 0;
        }

        /**
         * Method that returns if the File is an audio file.
         *
         * @param context The current context
         * @param file The File to check
         * @return boolean If the File is an audio file.
         */
        public static boolean isAudio(Context context, File file) {
            return MimeTypeHelper.getCategory(context, file).compareTo(MimeTypeCategory.AUDIO) == 0;
        }
    }
}
