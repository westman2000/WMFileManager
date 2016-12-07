package corp.wmsoft.android.examples.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.ui.FileManagerActivity;


public class FMActivity extends AppCompatActivity {

    private static final String TAG = "wmfm:FMActivity";


    int requestCodeDirectoryChooser = 123;
    int requestCodeFilePicker       = 125;
    int requestCodeSaveAsDialog     = 127;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentPanel, MainActivityFragment.newInstance())
                    .commit();
        }

        findViewById(R.id.dirChooser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showAsDirectoryChooser(FMActivity.this, requestCodeDirectoryChooser);
            }
        });
        findViewById(R.id.filePickerByMimeType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showAsFilePicker(FMActivity.this, requestCodeFilePicker, "image/jpeg");
            }
        });
        findViewById(R.id.filePickerAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection ConfusingArgumentToVarargsMethod
                WMFileManager.showAsFilePicker(FMActivity.this, requestCodeFilePicker, null);
            }
        });
        findViewById(R.id.saveAsDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(FMActivity.this, requestCodeSaveAsDialog, "test file name.txt");
            }
        });
        findViewById(R.id.saveAsDialogWithoutDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(FMActivity.this, requestCodeSaveAsDialog, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == requestCodeDirectoryChooser) {
                Toast.makeText(this, "DirectoryChooser: "+data.getStringExtra(FileManagerActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            } else if (requestCode == requestCodeFilePicker) {
                Toast.makeText(this, "FilePicker: "+data.getStringExtra(FileManagerActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            } else if (requestCode == requestCodeSaveAsDialog) {
                Toast.makeText(this, "SaveAsDialog: "+data.getStringExtra(FileManagerActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}