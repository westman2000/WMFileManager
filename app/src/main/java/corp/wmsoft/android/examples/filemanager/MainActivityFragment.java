package corp.wmsoft.android.examples.filemanager;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import corp.wmsoft.android.lib.filemanager.IOnChooseDirectoryListener;
import corp.wmsoft.android.lib.filemanager.WMFileManager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements IOnChooseDirectoryListener {

    private static final String TAG = "wmfm:MainFragment";


    public static MainActivityFragment newInstance() {
        MainActivityFragment frag = new MainActivityFragment();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WMFileManager.init(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.fileManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showDialogChooseFolder(getFragmentManager());
            }
        });

        return view;
    }

    @Override
    public void onDirectorySelected(String dir) {
        Log.d(TAG, "onDirectorySelected: "+dir);
    }
}
