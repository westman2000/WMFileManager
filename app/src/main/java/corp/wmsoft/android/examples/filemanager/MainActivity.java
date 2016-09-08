package corp.wmsoft.android.examples.filemanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import corp.wmsoft.android.lib.filemanager.FileManagerView;
import corp.wmsoft.android.lib.filemanager.IFileManagerEvent;
import corp.wmsoft.android.lib.filemanager.IFileManagerFileTimeFormat;
import corp.wmsoft.android.lib.filemanager.IFileManagerNavigationMode;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IOnFileManagerEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IOnFileManagerEventListener {

    /**/
    private static final int PERMISSIONS_REQUEST = 123;
    /**/
    private FileManagerView mNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNavigationView = (FileManagerView) findViewById(R.id.navigation_view);
        mNavigationView.setOnFileManagerEventListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        if (mNavigationView.getNavigationMode() == IFileManagerNavigationMode.ICONS)
            menu.findItem(R.id.action_icons).setChecked(true);
        else if (mNavigationView.getNavigationMode() == IFileManagerNavigationMode.SIMPLE)
            menu.findItem(R.id.action_simple).setChecked(true);
        else if (mNavigationView.getNavigationMode() == IFileManagerNavigationMode.DETAILS)
            menu.findItem(R.id.action_details).setChecked(true);

        // TODO - странная хуйня, гетер при первом запуске не срабатывает

        if (mNavigationView.getFileTimeFormat() == IFileManagerFileTimeFormat.SYSTEM)
            menu.findItem(R.id.action_system).setChecked(true);
        else if (mNavigationView.getFileTimeFormat() == IFileManagerFileTimeFormat.LOCALE)
            menu.findItem(R.id.action_locale).setChecked(true);
        else if (mNavigationView.getFileTimeFormat() == IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS)
            menu.findItem(R.id.action_ddmmyyyy).setChecked(true);
        else if (mNavigationView.getFileTimeFormat() == IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS)
            menu.findItem(R.id.action_mmddyyyy).setChecked(true);
        else if (mNavigationView.getFileTimeFormat() == IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS)
            menu.findItem(R.id.action_yyyymmdd).setChecked(true);

        MenuItem showHidden = menu.findItem(R.id.action_is_show_hidden);
        MenuItem showDirsFirst = menu.findItem(R.id.action_dirs_first);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_icons) {
            mNavigationView.setNavigationMode(IFileManagerNavigationMode.ICONS);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_simple) {
            mNavigationView.setNavigationMode(IFileManagerNavigationMode.SIMPLE);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_details) {
            mNavigationView.setNavigationMode(IFileManagerNavigationMode.DETAILS);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_system) {
            mNavigationView.setTimeFormat(IFileManagerFileTimeFormat.SYSTEM);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_locale) {
            mNavigationView.setTimeFormat(IFileManagerFileTimeFormat.LOCALE);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_ddmmyyyy) {
            mNavigationView.setTimeFormat(IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_mmddyyyy) {
            mNavigationView.setTimeFormat(IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS);
//            item.setChecked(true);
            return true;
        } else if (id == R.id.action_yyyymmdd) {
            mNavigationView.setTimeFormat(IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS);
//            item.setChecked(true);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            // We have requested multiple permissions for storage, so all of them need to be checked.
            if (verifyPermissions(grantResults)) {
                mNavigationView.onExternalStoragePermissionsGranted();
            } else {
                mNavigationView.onExternalStoragePermissionsNotGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onFileManagerEvent(@IFileManagerEvent int event) {
        switch (event) {
            case IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION:
                askExternalStoragePermission();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void askExternalStoragePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // Display a SnackBar with an explanation and a button to trigger the request.
                Snackbar.make(findViewById(R.id.fab), "To browse files, need access to file system", Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        },
                                        PERMISSIONS_REQUEST);
                            }
                        })
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        PERMISSIONS_REQUEST);
            }
        } else {
            mNavigationView.onExternalStoragePermissionsGranted();
        }
    }

    /**
     * Check that all given permissions have been granted by verifying that each entry in the
     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
     */
    private boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
