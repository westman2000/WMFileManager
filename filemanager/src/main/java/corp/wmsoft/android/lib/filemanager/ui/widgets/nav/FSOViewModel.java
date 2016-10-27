package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.databinding.ObservableField;
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
    public final ObservableField<String> itemSummary = new ObservableField<>();


    public FSOViewModel(FileSystemObject fso, String itemSize, String itemSummary) {
        this.fso = fso;
        this.itemSize = itemSize;
        this.itemSummary.set(itemSummary);
    }

}
