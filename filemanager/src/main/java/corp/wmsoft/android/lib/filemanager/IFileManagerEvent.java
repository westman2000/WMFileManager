package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * FileManagerView events, must be handled by user
 *
 * @see IFileManagerEvent#NEED_EXTERNAL_STORAGE_PERMISSION
 */
@Keep
@Documented
@IntDef({
        IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION
})
@Retention(RetentionPolicy.SOURCE)
public @interface IFileManagerEvent {

    /**
     * FileManagerView will fire this event if it need permission to external storage
     */
    int NEED_EXTERNAL_STORAGE_PERMISSION = 10;

}