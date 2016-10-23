package corp.wmsoft.android.lib.filemanager.interactors;

import android.os.FileObserver;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.mapper.FileSystemObjectMapper;
import corp.wmsoft.android.lib.filemanager.models.FileSystemObject;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerViewModel;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;
import rx.Observable;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 12:43 PM.<br/>
 */
// TODO - падает если удалять текущую папку и в ней много файлов
public class OnFileObserverEvent extends MVPCUseCase<OnFileObserverEvent.RequestValues, Boolean> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::OnFileObserver";


    public OnFileObserverEvent(IMVPCSchedulerProvider schedulerProvider) {
        super(schedulerProvider);

    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(final RequestValues requestValues) {

        return Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {

                        int newEvent = requestValues.getFileObserverEvent() & FileObserver.ALL_EVENTS;

                        if (requestValues.getEventPath() == null || newEvent <= 0)
                            return false;

                        Log.d(TAG, "event = "+newEvent);

                        switch (newEvent) {
                            case FileObserver.CREATE:
                            case FileObserver.MOVED_TO:
                                // add item
                                add(
                                    requestValues.getMonitoredDirPath() + File.separator + requestValues.getEventPath(),
                                    requestValues.getViewModel()
                                );
                                break;
                            case FileObserver.DELETE:
                            case FileObserver.MOVED_FROM:
                                // remove item
                                remove(
                                        requestValues.getEventPath(),
                                        requestValues.getViewModel()
                                );
                                break;
                            case FileObserver.DELETE_SELF:
                            case FileObserver.MOVE_SELF:
                                // remove and go up
                                removeSelf();
                                break;
                            case FileObserver.MODIFY:
                                // update
                                update();
                                break;
                        }

                        return true;
                    }
                });
    }

    private void add(String pathToAddedFile, FileManagerViewModel viewModel) {
        Log.d(TAG, "add("+pathToAddedFile+")");


        // TODO - add in sorted position, not in the end!!!

        FileSystemObject fsoToAdd = FileHelper.createFileSystemObject(pathToAddedFile);
        FSOViewModel fsoViewModel = FileSystemObjectMapper.mapToViewModel(fsoToAdd);

        viewModel.fsoViewModels.add(fsoViewModel);
    }

    private void remove(String eventPath, FileManagerViewModel viewModel) {
        Log.d(TAG, "remove("+eventPath+")");

        int size = viewModel.fsoViewModels.size();

        for (int i=size-1; i>=0; i--) {
            FSOViewModel fsoViewModel = viewModel.fsoViewModels.get(i);
            if (eventPath.equals(fsoViewModel.fso.name())) {
                viewModel.fsoViewModels.remove(i);
                return;
            }
        }
    }

    private void removeSelf() {
        Log.d(TAG, "removeSelf()");
        // TODO
        // TODO - when history will be - ADD go back here
    }

    private void update() {
        Log.d(TAG, "update()");
        // TODO
    }


    public static class RequestValues extends MVPCUseCase.RequestValues {

        /**/
        private final FileManagerViewModel viewModel;
        /**/
        private int fileObserverEvent;
        /**/
        private final String eventPath;
        /**/
        private final String monitoredDirPath;


        public RequestValues(FileManagerViewModel viewModel, int fileObserverEvent, @NonNull String eventPath, String monitoredDirPath) {
            this.viewModel          = viewModel;
            this.fileObserverEvent  = fileObserverEvent;
            this.eventPath          = eventPath;
            this.monitoredDirPath   = monitoredDirPath;
        }

        public FileManagerViewModel getViewModel() {
            return viewModel;
        }

        public int getFileObserverEvent() {
            return fileObserverEvent;
        }

        public String getEventPath() {
            return eventPath;
        }

        public String getMonitoredDirPath() {
            return monitoredDirPath;
        }
    }

}
