package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 10:40 AM.<br/>
 */
@Keep
public interface IOnFileManagerEventListener {
    /**
     * Method invoked when a request when the user choose a file.
     */
    void onFileManagerEvent(@IFileManagerEvent int event);
}
