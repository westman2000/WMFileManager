package corp.wmsoft.android.lib.filemanager.mapper;

import android.content.Context;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import rx.Observable;
import rx.functions.Func1;


/**
 * Created by admin on 10/20/16.
 */
public class FSOMapper {


    /**
     * Map data model to view model
     * @param fileSystemObject fso
     * @return view model
     */
    public static FSOViewModel mapToViewModel(FileSystemObject fileSystemObject) {

        Context context = WMFileManager.getApplicationContext();

        String summary;

        if (fileSystemObject.isParentDirectory())
            summary = WMFileManager.getApplicationContext().getString(R.string.wm_fm_parent_dir);
        else
            summary = FileHelper.formatFileTime(context, fileSystemObject.getLastModifiedTime());

        return new FSOViewModel(
                fileSystemObject,
                FileHelper.getHumanReadableSize(context, fileSystemObject),
                summary);
    }

    private static Observable<FSOViewModel> mapToViewModelObservable(FileSystemObject fileSystemObject) {
        return Observable.just(mapToViewModel(fileSystemObject));
    }

    /**
     * Map list of data models to view models
     * @param fileSystemObjects list of fso
     * @return view models
     */
    public static Observable<List<FSOViewModel>> mapToViewModels(List<FileSystemObject> fileSystemObjects) {

        return Observable
                .from(fileSystemObjects)
                .flatMap(new Func1<FileSystemObject, Observable<FSOViewModel>>() {
                    @Override
                    public Observable<FSOViewModel> call(FileSystemObject fileSystemObject) {
                        return mapToViewModelObservable(fileSystemObject);
                    }
                })
                .toList();
    }
}
