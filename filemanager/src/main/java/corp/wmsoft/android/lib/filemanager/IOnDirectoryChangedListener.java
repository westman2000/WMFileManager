package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 10:42 AM.<br/>
 * An interface to communicate a change of the current directory
 */
public interface IOnDirectoryChangedListener {
    /**
     * Method invoked when the current directory changes
     *
     * @param dir Full path to newly active directory
     */
    void onDirectoryChanged(String dir);
}
