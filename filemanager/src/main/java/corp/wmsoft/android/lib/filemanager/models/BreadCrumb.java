package corp.wmsoft.android.lib.filemanager.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import java.io.File;


/**
 * Created by admin on 10/21/16.
 */
@Keep
@AutoValue
public abstract class BreadCrumb {


    public abstract String fullPath();

    public abstract String title();


    @NonNull
    public static BreadCrumb create(String fullPath) {
        return new AutoValue_BreadCrumb(
                fullPath,
                new File(fullPath).getName()
        );
    }

}
