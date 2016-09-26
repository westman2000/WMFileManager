package corp.wmsoft.android.examples.filemanager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import corp.wmsoft.android.lib.filemanager.IFileManagerSortMode;
import corp.wmsoft.android.lib.filemanager.WMFileManager;
import corp.wmsoft.android.lib.filemanager.models.MountPoint;
import corp.wmsoft.android.lib.filemanager.ui.widgets.nav.IOnFileManagerEventListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**/
    private static final int FM_PERMISSIONS_REQUEST = 123;
    private static final int MP_PERMISSIONS_REQUEST = 456;
    /**/
    private FileManagerView mFileManagerView;
    private CustomNavigationView mCustomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCustomNavigationView = (CustomNavigationView) findViewById(R.id.nav_view);
        mCustomNavigationView.setNavigationItemSelectedListener(this);
        mCustomNavigationView.setOnFileManagerEventListener(new IOnFileManagerEventListener() {
            @Override
            public void onFileManagerEvent(@IFileManagerEvent int event) {
                if (event == IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION) {
                    mpAskExternalStoragePermission();
                }
            }
        });
        mCustomNavigationView.setOnMountPointSelected(new CustomNavigationView.OnMountPointSelected() {
            @Override
            public void onMountPointSelected(MountPoint mountPoint) {
                mFileManagerView.open(WMFileManager.createFileSystemObject(mountPoint.getPath()));
            }
        });

        mFileManagerView = (FileManagerView) findViewById(R.id.file_manager_view);
        mFileManagerView.setOnFileManagerEventListener(new IOnFileManagerEventListener() {
            @Override
            public void onFileManagerEvent(@IFileManagerEvent int event) {
                if (event == IFileManagerEvent.NEED_EXTERNAL_STORAGE_PERMISSION) {
                    fmAskExternalStoragePermission();
                }
            }
        });

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
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mFileManagerView.getNavigationMode() == IFileManagerNavigationMode.ICONS)
            menu.findItem(R.id.action_icons).setChecked(true);
        else if (mFileManagerView.getNavigationMode() == IFileManagerNavigationMode.SIMPLE)
            menu.findItem(R.id.action_simple).setChecked(true);
        else if (mFileManagerView.getNavigationMode() == IFileManagerNavigationMode.DETAILS)
            menu.findItem(R.id.action_details).setChecked(true);

        if (mFileManagerView.getFileTimeFormat() == IFileManagerFileTimeFormat.SYSTEM)
            menu.findItem(R.id.action_system).setChecked(true);
        else if (mFileManagerView.getFileTimeFormat() == IFileManagerFileTimeFormat.LOCALE)
            menu.findItem(R.id.action_locale).setChecked(true);
        else if (mFileManagerView.getFileTimeFormat() == IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS)
            menu.findItem(R.id.action_ddmmyyyy).setChecked(true);
        else if (mFileManagerView.getFileTimeFormat() == IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS)
            menu.findItem(R.id.action_mmddyyyy).setChecked(true);
        else if (mFileManagerView.getFileTimeFormat() == IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS)
            menu.findItem(R.id.action_yyyymmdd).setChecked(true);

        menu.findItem(R.id.action_is_show_hidden).setChecked(mFileManagerView.isShowHidden());
        menu.findItem(R.id.action_dirs_first).setChecked(mFileManagerView.isShowDirsFirst());

        if (mFileManagerView.getSortMode() == IFileManagerSortMode.NAME_ASC)
            menu.findItem(R.id.action_sort_by_name_asc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.NAME_DESC)
            menu.findItem(R.id.action_sort_by_name_desc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.DATE_ASC)
            menu.findItem(R.id.action_sort_by_date_asc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.DATE_DESC)
            menu.findItem(R.id.action_sort_by_date_desc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.SIZE_ASC)
            menu.findItem(R.id.action_sort_by_size_asc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.SIZE_DESC)
            menu.findItem(R.id.action_sort_by_size_desc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.TYPE_ASC)
            menu.findItem(R.id.action_sort_by_type_asc).setChecked(true);
        else if (mFileManagerView.getSortMode() == IFileManagerSortMode.TYPE_DESC)
            menu.findItem(R.id.action_sort_by_type_desc).setChecked(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_icons) {
            mFileManagerView.setNavigationMode(IFileManagerNavigationMode.ICONS);
            return true;
        } else if (id == R.id.action_simple) {
            mFileManagerView.setNavigationMode(IFileManagerNavigationMode.SIMPLE);
            return true;
        } else if (id == R.id.action_details) {
            mFileManagerView.setNavigationMode(IFileManagerNavigationMode.DETAILS);
            return true;
        } else if (id == R.id.action_system) {
            mFileManagerView.setTimeFormat(IFileManagerFileTimeFormat.SYSTEM);
            return true;
        } else if (id == R.id.action_locale) {
            mFileManagerView.setTimeFormat(IFileManagerFileTimeFormat.LOCALE);
            return true;
        } else if (id == R.id.action_ddmmyyyy) {
            mFileManagerView.setTimeFormat(IFileManagerFileTimeFormat.DDMMYYYY_HHMMSS);
            return true;
        } else if (id == R.id.action_mmddyyyy) {
            mFileManagerView.setTimeFormat(IFileManagerFileTimeFormat.MMDDYYYY_HHMMSS);
            return true;
        } else if (id == R.id.action_yyyymmdd) {
            mFileManagerView.setTimeFormat(IFileManagerFileTimeFormat.YYYYMMDD_HHMMSS);
            return true;
        } else if (id == R.id.action_is_show_hidden) {
            item.setChecked(!item.isChecked());
            mFileManagerView.setShowHidden(item.isChecked());
            return true;
        } else if (id == R.id.action_dirs_first) {
            item.setChecked(!item.isChecked());
            mFileManagerView.setShowDirsFirst(item.isChecked());
            return true;
        } else if (id == R.id.action_sort_by_name_asc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.NAME_ASC);
            return true;
        } else if (id == R.id.action_sort_by_name_desc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.NAME_DESC);
            return true;
        } else if (id == R.id.action_sort_by_date_asc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.DATE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_date_desc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.DATE_DESC);
            return true;
        } else if (id == R.id.action_sort_by_size_asc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.SIZE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_size_desc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.SIZE_DESC);
            return true;
        } else if (id == R.id.action_sort_by_type_asc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.TYPE_ASC);
            return true;
        } else if (id == R.id.action_sort_by_type_desc) {
            mFileManagerView.setSortMode(IFileManagerSortMode.TYPE_DESC);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        mCustomNavigationView.onMountPointSelect(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FM_PERMISSIONS_REQUEST) {
            // We have requested multiple permissions for storage, so all of them need to be checked.
            if (verifyPermissions(grantResults)) {
                mFileManagerView.onExternalStoragePermissionsGranted();
            } else {
                mFileManagerView.onExternalStoragePermissionsNotGranted();
            }
        } else if (requestCode == MP_PERMISSIONS_REQUEST) {
            // We have requested multiple permissions for storage, so all of them need to be checked.
            if (verifyPermissions(grantResults)) {
                mCustomNavigationView.onExternalStoragePermissionsGranted();
            } else {
                mCustomNavigationView.onExternalStoragePermissionsNotGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void fmAskExternalStoragePermission() {
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
                                        FM_PERMISSIONS_REQUEST);
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
                        FM_PERMISSIONS_REQUEST);
            }
        } else {
            mFileManagerView.onExternalStoragePermissionsGranted();
        }
    }

    private void mpAskExternalStoragePermission() {
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
                                        MP_PERMISSIONS_REQUEST);
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
                        MP_PERMISSIONS_REQUEST);
            }
        } else {
            mCustomNavigationView.onExternalStoragePermissionsGranted();
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
