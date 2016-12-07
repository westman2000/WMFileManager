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
                WMFileManager.showAsDirectoryChooser(FMActivity.this);
            }
        });
        findViewById(R.id.filePickerByMimeType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showAsFilePicker(FMActivity.this, "image/jpeg");
            }
        });
        findViewById(R.id.filePickerAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection ConfusingArgumentToVarargsMethod
                WMFileManager.showAsFilePicker(FMActivity.this, null);
            }
        });
        findViewById(R.id.saveAsDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(FMActivity.this, "test file name.txt");
            }
        });
        findViewById(R.id.saveAsDialogWithoutDefault).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WMFileManager.showFileSaveAsDialog(FMActivity.this, null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == FileManagerActivity.REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, data.getStringExtra(FileManagerActivity.EXTRA_RESULT), Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}