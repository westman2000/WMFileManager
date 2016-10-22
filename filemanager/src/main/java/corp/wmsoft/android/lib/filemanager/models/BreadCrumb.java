package corp.wmsoft.android.lib.filemanager.models;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;


/**
 * Created by admin on 10/21/16.
 */
public class BreadCrumb {

    /**/
    private String currentDirectory;


    public BreadCrumb(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    @Nullable
    public String getTitle() {
        if (!TextUtils.isEmpty(currentDirectory)) {
            return new File(this.currentDirectory).getName();
        }
        return null;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    @Override
    public String toString() {
        return '[' + getTitle() + ']';
    }
}
