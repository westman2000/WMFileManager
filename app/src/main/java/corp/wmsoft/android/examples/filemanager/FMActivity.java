package corp.wmsoft.android.examples.filemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import corp.wmsoft.android.lib.filemanager.IOnChooseDirectoryListener;
import corp.wmsoft.android.lib.filemanager.WMFileManager;


public class FMActivity extends AppCompatActivity implements IOnChooseDirectoryListener {

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
            WMFileManager.showDialogChooseFolder(getSupportFragmentManager());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDirectorySelected(String dir) {
        Log.d(TAG, "onDirectoryChanged: "+dir);
    }

}