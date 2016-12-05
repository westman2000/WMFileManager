package corp.wmsoft.android.lib.filemanager.ui;

import android.support.annotation.Keep;

import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;


/**
 * Listener for breadcrumb long click - to show full path
 */
@Keep
public interface IBreadCrumbListener {

    boolean onBreadCrumbLongClick(BreadCrumb breadCrumb);

}
