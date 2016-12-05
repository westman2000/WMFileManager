package corp.wmsoft.android.lib.filemanager.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


/**
 * Created by admin on 12/5/16.
 */
public class FileManagerActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

    /**/
    private static final String TAG = "wmfm::Activity";

    /**/
    public static final String EXTRA_TITLE = "EXTRA_TITLE";


    public static void startAsDialogActivity(Context context, String title) {
        Intent intent = new Intent(context, FileManagerActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        Log.d(TAG, "before super onCreate: "+getIntent());

        super.onCreate(savedInstanceState);

        String title = getIntent().getStringExtra(EXTRA_TITLE);

        FileManagerFragment.showFileManager(getSupportFragmentManager(), title);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }
}
