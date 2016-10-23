package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;


/**
 * Listener for breadcrumb long click - to show full path
 */
public interface IBreadCrumbListener {

    boolean onBreadCrumbLongClick(BreadCrumb breadCrumb);

}
