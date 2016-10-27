package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;
import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;


/**
 * Created by admin on 10/20/16.
 */
public class FileManagerViewModel {

    /**/
    public final ObservableList<FSOViewModel> fsoViewModels = new ObservableArrayList<>();

    /**/
    final ObservableList<BreadCrumb> breadCrumbs = new ObservableArrayList<>();

    /**/
    public final ObservableList<MountPoint> mountPoints = new ObservableArrayList<>();

    /**/
    MountPoint selectedMountPoint;

    // показывать ли процесс загрузки
    @Keep
    public final ObservableBoolean isLoading = new ObservableBoolean(true);

    @Keep
    public final ObservableBoolean isEmptyFolder = new ObservableBoolean(false);

}
