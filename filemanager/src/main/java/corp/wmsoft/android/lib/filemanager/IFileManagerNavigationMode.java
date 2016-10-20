package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Possible navigation modes
 *
 * @see IFileManagerNavigationMode#UNDEFINED
 * @see IFileManagerNavigationMode#ICONS
 * @see IFileManagerNavigationMode#SIMPLE
 * @see IFileManagerNavigationMode#DETAILS
 */
@Keep
@Documented
@IntDef({
        IFileManagerNavigationMode.UNDEFINED,
        IFileManagerNavigationMode.ICONS,
        IFileManagerNavigationMode.SIMPLE,
        IFileManagerNavigationMode.DETAILS
})
@Retention(RetentionPolicy.SOURCE)
public @interface IFileManagerNavigationMode {

    /**
     * Undefined mode, used internally for first start
     */
    int UNDEFINED = 0;
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