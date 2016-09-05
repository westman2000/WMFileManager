package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <br/>Created by WestMan2000 on 9/1/16 at 11:31 AM.<br/>
 */
@IntDef({
        IFileManagerNavigationMode.ICONS,
        IFileManagerNavigationMode.SIMPLE,
        IFileManagerNavigationMode.DETAILS
})
@Retention(RetentionPolicy.SOURCE)
public @interface IFileManagerNavigationMode {

    /**
     * That mode shows a icon based view (icon + name) with a {@link GridLayoutManager}.
     */
    int ICONS = 10;
    /**
     * That mode shows a simple item view (icon + name) with a {@link LinearLayoutManager}.
     */
    int SIMPLE = 20;
    /**
     * That mode shows a detail item view (icon + name + last modification + size)
     * with a {@link LinearLayoutManager}.
     */
    int DETAILS = 30;

}
