package corp.wmsoft.android.examples.filemanager;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import corp.wmsoft.android.lib.filemanager.IOnFilePickedListener;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.ui.FileManagerActivity;

import static android.app.Activity.RESULT_OK;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        // Check which request we're responding to
        if (requestCode == FileManagerActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), data.getStringExtra(FileManagerActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        view.findViewById(R.id.dirChooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFolderDlg();
            }
        });
        view.findViewById(R.id.filePickerByMimeType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showAsFilePicker(MainActivityFragment.this, "image/jpeg");
            }
        });
        view.findViewById(R.id.filePickerAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection ConfusingArgumentToVarargsMethod
                WMFileManager.showAsFilePicker(MainActivityFragment.this, null);
            }
        });
        view.findViewById(R.id.saveAsDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(MainActivityFragment.this, "test file name.txt");
            }
        });
        view.findViewById(R.id.saveAsDialogWithoutDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(MainActivityFragment.this, null);
            }
        });


        return view;
    }

    private void chooseFolderDlg() {
        WMFileManager.showAsDirectoryChooser(this);
    }
}
