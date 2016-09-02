package corp.wmsoft.android.lib.filemanager.interactors;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.models.ParentDirectory;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpc.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpc.util.IMVPCSchedulerProvider;
import rx.Observable;
import rx.Subscriber;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 12:43 PM.<br/>
 */
public class GetFSOList extends MVPCUseCase<GetFSOList.RequestValues, List<FileSystemObject>> {


    public GetFSOList(IMVPCSchedulerProvider schedulerProvider) {
        super(schedulerProvider);

    }

    @Override
    public Observable<List<FileSystemObject>> buildUseCaseObservable(final RequestValues requestValues) {
        return Observable.create(new Observable.OnSubscribe<List<FileSystemObject>>(){

            @Override
            public void call(Subscriber<? super List<FileSystemObject>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    File f = new File(requestValues.getSrc());
                    if (!f.exists()) {
                        subscriber.onError(new Error("Result: FAIL. NoSuchFileOrDirectory"));
                    }

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
                    if (requestValues.getSrc() != null &&
                            requestValues.getSrc().compareTo(FileHelper.ROOT_DIRECTORY) != 0 ) {
                        fsoList.add(0, new ParentDirectory(new File(requestValues.getSrc()).getParent()));
                    }

                    subscriber.onNext(fsoList);
                    subscriber.onCompleted();
                }
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
