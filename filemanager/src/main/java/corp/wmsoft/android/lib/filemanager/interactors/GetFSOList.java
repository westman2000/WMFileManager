package corp.wmsoft.android.lib.filemanager.interactors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.mapper.FileSystemObjectMapper;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.rx.RxStorageHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;

import rx.Observable;
import rx.functions.Func1;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 12:43 PM.<br/>
 */
public class GetFSOList extends MVPCUseCase<GetFSOList.RequestValues, List<FSOViewModel>> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::GetFSOList";


    public GetFSOList(IMVPCSchedulerProvider schedulerProvider) {
        super(schedulerProvider);

    }

    @Override
    public Observable<List<FSOViewModel>> buildUseCaseObservable(final RequestValues requestValues) {

        return Observable.fromCallable(new Callable<List<FileSystemObject>>() {
                    @Override
                    public List<FileSystemObject> call() throws Exception {

                        File f = new File(requestValues.getSrc());

                        File[] files = f.listFiles();

                        List<FileSystemObject> fsoList = new ArrayList<>();

                        if (files != null) {
                            for (File file : files) {
                                FileSystemObject fso = FileHelper.createFileSystemObject(file);
                                if (fso != null) {
                                    fsoList.add(fso);
                                }
                            }
                        }

                        //Now if not is the root directory
                        if (!RxStorageHelper.isStorageVolume(requestValues.getSrc())) {
                            fsoList.add(0, FileSystemObject.createParentDirectory(new File(requestValues.getSrc()).getParent())
                            );
                        }

                        //Apply user preferences
                        return FileHelper.applyUserPreferences(fsoList);
                    }
                })
                .flatMap(new Func1<List<FileSystemObject>, Observable<List<FSOViewModel>>>() {
                    @Override
                    public Observable<List<FSOViewModel>> call(List<FileSystemObject> fileSystemObjects) {
                        return FileSystemObjectMapper.mapToViewModels(fileSystemObjects);
                    }
                });
    }


    public static class RequestValues extends MVPCUseCase.RequestValues {

        /**/
        private final String mSrc;


        public RequestValues(String src) {
            this.mSrc = src;
        }

        public String getSrc() {
            return mSrc;
        }

    }

}
