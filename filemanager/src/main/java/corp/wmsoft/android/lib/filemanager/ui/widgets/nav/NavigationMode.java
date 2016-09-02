package corp.wmsoft.android.lib.filemanager.ui.widgets.nav;

import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <br/>Created by WestMan2000 on 9/1/16 at 11:31 AM.<br/>
 */
@IntDef({NavigationMode.ICONS, NavigationMode.SIMPLE, NavigationMode.DETAILS})
@Retention(RetentionPolicy.SOURCE)
public @interface NavigationMode {

    /**
     * That mode shows a icon based view (icon + name) with a {@link GridLayoutManager}.
     */
    int ICONS = 10;
    /**
     * That mode shows a simple item view (icon + name) with a {@link LinearLayoutManager}.
     */
    int SIMPLE = 20;
    /**
     * That mode shows a detail item view (icon + name + last modification + permissions + size)
     * with a {@link LinearLayoutManager}.
     */
    int DETAILS = 30;

}
