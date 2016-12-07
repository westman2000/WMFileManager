package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;

/**
 * An interface to send current selected directory
 */
@Deprecated
public interface IOnChooseDirectoryListener {
    /**
     * Method invoked when dialog closing
     *
     * @param dir Full path to selected directory
     */
    void onDirectorySelected(String dir);
}
