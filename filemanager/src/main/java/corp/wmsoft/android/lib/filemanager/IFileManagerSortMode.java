package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 11:31 AM.<br/>
 *
 * Navigation sort modes.
 */
@IntDef({
        IFileManagerSortMode.NAME_ASC,
        IFileManagerSortMode.NAME_DESC,
        IFileManagerSortMode.DATE_ASC,
        IFileManagerSortMode.DATE_DESC,
        IFileManagerSortMode.SIZE_ASC,
        IFileManagerSortMode.SIZE_DESC,
        IFileManagerSortMode.TYPE_ASC,
        IFileManagerSortMode.TYPE_DESC
})
@Retention(RetentionPolicy.SOURCE)
@Keep
public @interface IFileManagerSortMode {

    /**
     * That mode sorts objects by name (ascending).
     */
    int NAME_ASC    = 1000;
    /**
     * That mode sorts objects by name (descending).
     */
    int NAME_DESC   = 1001;
    /**
     * That mode sorts objects by date (ascending).
     */
    int DATE_ASC    = 1002;
    /**
     * That mode sorts objects by date (descending).
     */
    int DATE_DESC   = 1003;
    /**
     * That mode sorts objects by size (ascending).
     */
    int SIZE_ASC    = 1004;
    /**
     * That mode sorts objects by size (descending).
     */
    int SIZE_DESC   = 1005;
    /**
     * That mode sorts objects by type (ascending).
     */
    int TYPE_ASC    = 1006;
    /**
     * That mode sorts objects by type (descending).
     */
    int TYPE_DESC   = 1007;

}