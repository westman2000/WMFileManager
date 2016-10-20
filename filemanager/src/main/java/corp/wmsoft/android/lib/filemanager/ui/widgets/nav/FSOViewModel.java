package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;


/**
 * Created by admin on 10/20/16.
 */
@Keep
public class FSOViewModel {

    /**/
    public FileSystemObject fso;

    /**/
    public String itemSize;

    /**/
    public String itemSummary;


    public FSOViewModel(FileSystemObject fso, String itemSize, String itemSummary) {
        this.fso = fso;
        this.itemSize = itemSize;
        this.itemSummary = itemSummary;
    }

}
