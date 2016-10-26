package corp.wmsoft.android.examples.filemanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import corp.wmsoft.android.lib.filemanager.IOnChooseDirectoryListener;
import corp.wmsoft.android.lib.filemanager.IOnFilePickedListener;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.FileManagerFragment;


public class FMActivity extends AppCompatActivity implements IOnChooseDirectoryListener, IOnFilePickedListener {

    private static final String TAG = "FMActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show_as_dialog) {
            WMFileManager.setRestrictions(WMFileManager.createRestrictionOnlyImages());
            FileManagerFragment.showAsDialog(getSupportFragmentManager());
            return true;
        } else if (id == R.id.action_show_as_fragment) {
            WMFileManager.setRestrictions(WMFileManager.createRestrictionOnlyTorrents());
            FileManagerFragment.replaceInFragmentManager(getSupportFragmentManager(), R.id.contentPanel);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDirectorySelected(String dir) {
        Log.d(TAG, "onDirectoryChanged: "+dir);
    }

    @Override
    public void onFilePicked(String file) {
        Log.d(TAG, "onFilePicked: "+file);
    }
}