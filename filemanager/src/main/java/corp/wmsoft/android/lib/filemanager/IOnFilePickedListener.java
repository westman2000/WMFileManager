package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;

/**
 * <br/>Created by WestMan2000 on 9/2/16 at 10:41 AM.<br/>
 * An interface to communicate a request when the user choose a file.
 */
@Deprecated
public interface IOnFilePickedListener {
    /**
     * Method invoked when a request when the user choose a file.
     *
     * @param file Full path to choose file
     */
    void onFilePicked(String file);
}
