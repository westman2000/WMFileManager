package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 10:42 AM.<br/>
 * An interface to communicate a change of the current directory
 */
public interface IOnDirectoryChangedListener {
    /**
     * Method invoked when the current directory changes
     *
     * @param fso The newly active directory
     */
    void onDirectoryChanged(FileSystemObject fso);
}
