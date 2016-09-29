package corp.wmsoft.android.lib.filemanager.interactors;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.filemanager.util.rx.RxStorageHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;
import rx.Observable;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 12:43 PM.<br/>
 */
public class GetFSOList extends MVPCUseCase<GetFSOList.RequestValues, List<FileSystemObject>> {

    /**/
    private static final String TAG = "WMFM::GetFSOList";


    public GetFSOList(IMVPCSchedulerProvider schedulerProvider) {
        super(schedulerProvider);

    }

    @Override
    public Observable<List<FileSystemObject>> buildUseCaseObservable(final RequestValues requestValues) {
        return Observable.fromCallable(new Callable<List<FileSystemObject>>() {
            @Override
            public List<FileSystemObject> call() throws Exception {

                Log.d(TAG, "getSrc="+requestValues.getSrc());

                File f = new File(requestValues.getSrc());

                File[] files = f.listFiles();

                Log.d(TAG, "files="+ Arrays.toString(files));

                List<FileSystemObject> fsoList = new ArrayList<>();

                if (files != null) {
                    for (File file : files) {
                        FileSystemObject fso = FileHelper.createFileSystemObject(file);
                        if (fso != null) {
                            fsoList.add(fso);
                        }
                    }
                }

                Log.d(TAG, "fsoList="+ fsoList);

                //Now if not is the root directory
                if (!RxStorageHelper.isStorageVolume(requestValues.getSrc())) {
                    fsoList.add(0, new ParentDirectory(new File(requestValues.getSrc()).getParent()));
                }

                Log.d(TAG, "===========");

                //Apply user preferences
                List<FileSystemObject> sortedFiles = FileHelper.applyUserPreferences(fsoList);

                Log.d(TAG, "sortedFiles="+sortedFiles);

                return sortedFiles;
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
