package corp.wmsoft.android.lib.filemanager.interactors;

import android.content.Context;

import java.util.concurrent.Callable;

import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FSOViewModel;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerViewModel;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;
import corp.wmsoft.android.lib.mvpcrx.interactor.MVPCUseCase;
import corp.wmsoft.android.lib.mvpcrx.util.IMVPCSchedulerProvider;

import rx.Observable;


/**
 * <br/>Created by WestMan2000 on 8/31/16 at 12:43 PM.<br/>
 */
public class UpdateListSummary extends MVPCUseCase<UpdateListSummary.RequestValues, Boolean> {

    /**/
    @SuppressWarnings("unused")
    private static final String TAG = "wmfm::UpdateListSummary";

    /**/
    private final Context mContext;


    public UpdateListSummary(IMVPCSchedulerProvider schedulerProvider, Context context) {
        super(schedulerProvider);
        mContext = context.getApplicationContext();
    }

    @Override
    public Observable<Boolean> buildUseCaseObservable(final RequestValues requestValues) {

        return Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {

                        FileManagerViewModel viewModel = requestValues.getViewModel();

                        for (FSOViewModel fsoViewModel : viewModel.fsoViewModels) {
                            fsoViewModel.itemSummary.set(FileHelper.getFsoSummary(mContext, fsoViewModel.fso));
                        }

                        return true;
                    }
                });
    }


    public static class RequestValues extends MVPCUseCase.RequestValues {

        /**/
        private final FileManagerViewModel mViewModel;


        public RequestValues(FileManagerViewModel viewModel) {
            this.mViewModel = viewModel;
        }

        public FileManagerViewModel getViewModel() {
            return mViewModel;
        }
    }

}
