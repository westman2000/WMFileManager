package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * <br/>Created by WestMan2000 on 9/1/16 at 11:31 AM.<br/>
 */
@IntDef({
        IFileManagerFileTimeFormat.SYSTEM,
        IFileManagerFileTimeFormat.LOCALE,
        IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS,
        IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS,
        IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS
})
@Retention(RetentionPolicy.SOURCE)
@Keep
public @interface IFileManagerFileTimeFormat {

    int SYSTEM          = 100;

    int LOCALE          = 200;

    int DDMMYYYY_HHMMSS = 300; // dd/MM/yyyy HH:mm:ss

    int MMDDYYYY_HHMMSS = 400; // MM/dd/yyyy HH:mm:ss

    int YYYYMMDD_HHMMSS = 500; // yyyy-MM-dd HH:mm:ss

}
