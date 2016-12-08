package corp.wmsoft.android.lib.filemanager.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import corp.wmsoft.android.lib.filemanager.R;
import corp.wmsoft.android.lib.filemanager.util.FileHelper;


/**
 *
 */
public class CreateNewFolderDialogFragment extends DialogFragment implements TextView.OnEditorActionListener, TextWatcher {

    /**/
    private static final String TAG  = "wmfm::NewFolder";
    /**/
    private static final String KEY_FOLDERS  = "KEY_FOLDERS";


    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks. */
    public interface CreateNewFolderDialogListener {

        void onNewFolderCreated(String folderName);

    }

    // Use this instance of the interface to deliver action events
    CreateNewFolderDialogListener createNewFolderDialogListener;

    /**/
    private AlertDialog mAlertDialog;

    /**/
    private TextInputLayout tilNewFolderName;
    private EditText newFolderEditText;

    /**/
    private ArrayList<String> currentPathFolders;


    public static void show(FragmentManager manager, ArrayList<String> currentPathFolders) {
        // for not show it twice
        if (manager.findFragmentByTag(TAG) == null) {
            CreateNewFolderDialogFragment createNewFolderDialogFragment = new CreateNewFolderDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(KEY_FOLDERS, currentPathFolders);
            createNewFolderDialogFragment.setArguments(args);
            createNewFolderDialogFragment.show(manager, TAG);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        currentPathFolders = getArguments().getStringArrayList(KEY_FOLDERS);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.wm_fm_hint_new_folder);
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                attemptSubmit(newFolderEditText.getText().toString());
            }
        });

        @SuppressLint("InflateParams")
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.wm_fm_create_new_folder, null);
        newFolderEditText = (EditText) rootView.findViewById(R.id.newFolderName);
        tilNewFolderName = (TextInputLayout) rootView.findViewById(R.id.tilNewFolderName);
        newFolderEditText.addTextChangedListener(this);
        newFolderEditText.setOnEditorActionListener(this);
        builder.setView(rootView);

        mAlertDialog = builder.create();

        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow: "+newFolderEditText.getText().toString());
                mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isNewFolderNameValid(newFolderEditText.getText().toString()));
            }
        });

        return mAlertDialog;
    }

    // Override the Fragment.onAttach() method to instantiate the CreateNewFolderDialogFragment
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            createNewFolderDialogListener = (CreateNewFolderDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement CreateNewFolderDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        createNewFolderDialogListener = null;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {

            String folderName = newFolderEditText.getText().toString();
            if (isNewFolderNameValid(folderName))
                attemptSubmit(folderName);

            return true;
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(isNewFolderNameValid(newFolderEditText.getText().toString()));
    }

    private boolean isNewFolderNameValid(String folderName) {

        if (TextUtils.isEmpty(folderName.trim())) {
            tilNewFolderName.setError(null);
            return false;
        }

        for (String fName : currentPathFolders) {
            if (folderName.equalsIgnoreCase(fName)) {
                tilNewFolderName.setError(getContext().getString(R.string.wm_fm_error_folder_exist));
                return false;
            }
        }

        if (FileHelper.containsIllegals(folderName)) {
            tilNewFolderName.setError(getContext().getString(R.string.wm_fm_error_folder_name_illegal));
            return false;
        }

        tilNewFolderName.setError(null);
        return true;
    }

    private void attemptSubmit(String folderName) {
        if (createNewFolderDialogListener != null)
            createNewFolderDialogListener.onNewFolderCreated(folderName);
        dismiss();
    }
}