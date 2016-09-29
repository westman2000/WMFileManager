package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Created by WestMan2000 on 9/28/16. <p>
 *
 *     An enumeration of the restrictions that can be applied when displaying list of files.
 */
@IntDef({
        IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION,
        IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION,
        IFileManagerDisplayRestrictions.SIZE_RESTRICTION,
        IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION
})
@Retention(RetentionPolicy.SOURCE)
@Keep
public @interface IFileManagerDisplayRestrictions {

    /**
     * Restriction for display only files with the category.
     */
    int CATEGORY_TYPE_RESTRICTION           = 0;
    /**
     * Restriction for display only files with these mime/types (this restriction
     * accepts a String or String[] as parameter).
     */
    int MIME_TYPE_RESTRICTION               = 1;
    /**
     * Restriction for display only files with a size lower than the specified
     */
    int SIZE_RESTRICTION                    = 2;
    /**
     * Restriction for display only directories
     */
    int DIRECTORY_ONLY_RESTRICTION          = 3;

}