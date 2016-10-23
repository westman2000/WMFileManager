package corp.wmsoft.android.lib.filemanager;

import corp.wmsoft.android.lib.filemanager.models.BreadCrumb;


/**
 * Interface with events from a breadcrumb.
 */
public interface BreadcrumbListener {
    /**
     * This method is fired when a breadcrumb item is clicked.
     *
     * @param item The breadcrumb item click
     */
    void onBreadcrumbItemClick(BreadCrumb item);
}
