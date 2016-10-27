package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Possible file time formats to show
 *
 * @see IFileManagerFileTimeFormat#SYSTEM
 * @see IFileManagerFileTimeFormat#LOCALE
 * @see IFileManagerFileTimeFormat#DDMMYYYY_HHMMSS
 * @see IFileManagerFileTimeFormat#MMDDYYYY_HHMMSS
 * @see IFileManagerFileTimeFormat#YYYYMMDD_HHMMSS
 */
@Documented
@IntDef({
        IFileManagerFileTimeFormat.SYSTEM,
        IFileManagerFileTimeFormat.LOCALE,
        IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS,
        IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS,
        IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS
})
@Retention(RetentionPolicy.SOURCE)
public @interface IFileManagerFileTimeFormat {

    /**
     * by system
     */
    int SYSTEM          = 100;

    /**
     * by current locale
     */
    int LOCALE          = 200;

    /**
     * format: dd/MM/yyyy HH:mm:ss
     */
    int DDMMYYYY_HHMMSS = 300;

    /**
     * format: MM/dd/yyyy HH:mm:ss
     */
    int MMDDYYYY_HHMMSS = 400;

    /**
     * format: yyyy-MM-dd HH:mm:ss
     */
    int YYYYMMDD_HHMMSS = 500;

}
