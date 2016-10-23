package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;


/**
 * Created by admin on 10/20/16.
 */
public class FileManagerViewModel {

    /**/
    public final ObservableList<FSOViewModel> fsoViewModels = new ObservableArrayList<>();

    /**/
    public final ObservableList<BreadCrumb> breadCrumbs = new ObservableArrayList<>();

    // показывать ли процесс загрузки
    public final ObservableBoolean isLoading = new ObservableBoolean(true);

    public final ObservableBoolean isEmptyFolder = new ObservableBoolean(false);

}
