package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <br/>Created by WestMan2000 on 9/1/16 at 11:31 AM.<br/>
 */
@IntDef({IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION})
@Retention(RetentionPolicy.SOURCE)
public @interface IFileManagerEvent {

    /**
     * FileManagerView will fire this event if it need permission to external storage
     */
    int NEED_EXTERNAL_STORAGE_PERMISSION = 10;

}
