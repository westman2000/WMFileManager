package corp.wmsoft.android.lib.filemanager.mapper;

import android.content.Context;

import java.util.List;

import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by admin on 10/21/16.
 */
public class FileSystemObjectMapper {

    /**
     * Map data model to view model
     * @param fileSystemObject fso
     * @return view model
     */
    public static FSOViewModel mapToViewModel(FileSystemObject fileSystemObject) {

        Context context = WMFileManager.getApplicationContext();

        return new FSOViewModel(
                fileSystemObject,
                FileHelper.getHumanReadableSize(context, fileSystemObject),
                FileHelper.getFsoSummary(context, fileSystemObject));
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
