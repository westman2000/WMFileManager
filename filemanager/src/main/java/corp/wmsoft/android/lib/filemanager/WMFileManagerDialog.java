package corp.wmsoft.android.lib.filemanager;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.MimeTypeHelper;


/**
 * Helper class to show file manager as dialog
 */
public class WMFileManagerDialog {


    /**
     * Show dialog to choose folder caller (Activity or Fragment) must implement
     * {@link IOnChooseDirectoryListener} to enable button "choose" on dialog and receive choose event
     *
     * @param fragmentManager caller FragmentManager
     * @param dialogTitle dialog title
     */
    @Keep
    public static void chooseFolder(@NonNull FragmentManager fragmentManager, @NonNull String dialogTitle) {
        // set restrictions
        setRestrictionOnlyDirectory();

//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        Fragment prev = fragmentManager.findFragmentByTag(FileManagerFragment.TAG);
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        DialogFragment newFragment = FileManagerFragment.newInstance(dialogTitle);
//        newFragment.show(ft, FileManagerFragment.TAG);
    }

    /**
     * For activities.
     * Show dialog to choose file. You can specify which file type to show by passing array of mime
     * types, only this file types will be visible. Activity must implement {@link IOnFilePickedListener}
     * to be able receive event when file is picked.
     *
     * @param fragmentManager caller FragmentManager
     * @param dialogTitle dialog title
     * @param mimeTypes array of mime types strings. if null or empty - all file types will be visible
     */
    @Keep
    public static void chooseFileByMimeType(@NonNull FragmentManager fragmentManager, @NonNull String dialogTitle, @Nullable String... mimeTypes) {
        chooseFileByMimeType(fragmentManager, null, dialogTitle, mimeTypes);
    }

    /**
     * For fragments.
     * Show dialog to choose file. You can specify which file type to show by passing array of mime
     * types, only this file types will be visible. Fragment must implement {@link IOnFilePickedListener}
     * to be able receive event when file is picked.
     *
     * @param fragmentManager caller FragmentManager
     * @param targetFragment fragment which implements {@link IOnFilePickedListener}
     * @param dialogTitle dialog title
     * @param mimeTypes array of mime types strings. if null or empty - all file types will be visible
     */
    @Keep
    public static void chooseFileByMimeType(@NonNull FragmentManager fragmentManager, Fragment targetFragment, @NonNull String dialogTitle, @Nullable String... mimeTypes) {
        // set restrictions
        setRestrictionForMimeTypes(mimeTypes);
//
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        Fragment prev = fragmentManager.findFragmentByTag(FileManagerFragment.TAG);
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        // Create and show the dialog.
//        DialogFragment newFragment = FileManagerFragment.newInstance(dialogTitle);
//
//        if (targetFragment != null)
//            newFragment.setTargetFragment(targetFragment, 0);
//
//        newFragment.show(ft, FileManagerFragment.TAG);
    }

    private static void setRestrictionOnlyDirectory() {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.DIRECTORY_ONLY_RESTRICTION, true);
        FileHelper.setRestrictions(restrictions);
    }

    // "application/x-bittorrent"
    // "tox/x-profile"
    private static void setRestrictionForMimeTypes(@Nullable String... mimeTypes) {

        if (mimeTypes == null) return;

        if (mimeTypes.length == 0) return;

        SparseArray<Object> restrictions = new SparseArray<>();
        for (String mimeType : mimeTypes) {
            restrictions.put(IFileManagerDisplayRestrictions.MIME_TYPE_RESTRICTION, mimeType);
        }

        FileHelper.setRestrictions(restrictions);
    }

    private static SparseArray createRestrictionForMimeTypeCategory(MimeTypeHelper.MimeTypeCategory mimeTypeCategory) {
        SparseArray<Object> restrictions = new SparseArray<>();
        restrictions.put(IFileManagerDisplayRestrictions.CATEGORY_TYPE_RESTRICTION, mimeTypeCategory);
        return restrictions;
    }
}
