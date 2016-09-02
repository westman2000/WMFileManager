package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 10:41 AM.<br/>
 * An interface to communicate a request when the user choose a file.
 */
public interface IOnFilePickedListener {
    /**
     * Method invoked when a request when the user choose a file.
     *
     * @param fso The item choose
     */
    void onFilePicked(FileSystemObject fso);
}
