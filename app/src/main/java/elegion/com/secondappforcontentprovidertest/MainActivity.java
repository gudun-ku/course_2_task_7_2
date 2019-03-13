package elegion.com.secondappforcontentprovidertest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
 View.OnClickListener {
    private final int LOADER_ID = 12;

    private final String ACTION_QUERY = getString(R.string.str_action_query);
    private final String ACTION_INSERT = getString(R.string.str_action_insert);
    private final String ACTION_UPDATE = getString(R.string.str_action_update);
    private final String ACTION_DELETE = getString(R.string.str_action_delete);

    private Spinner mTableSpinner;
    private Spinner mActionSpinner;
    private EditText mEditId, mEditData1, mEditData2, mEditData3;
    private Loader mLoader;
    private Toast mToast;

    private void showToast(String msg) {
        if (mToast != null) mToast.cancel();

        mToast = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onClick(View v) {


        String[] tables =  getResources().getStringArray(R.array.tables);
        String action = mActionSpinner.getSelectedItem().toString();
        String table = tables[(int) mTableSpinner.getSelectedItemId()];

        if (action.equals(ACTION_QUERY)) {

            Bundle args = new Bundle();
            args.putString("ACTION", action);
            args.putString("TABLE", table);
            args.putString("ID", mEditId.getText().toString());
            args.putString("DATA1", mEditData1.getText().toString());
            args.putString("DATA2", mEditData2.getText().toString());
            args.putString("DATA3", mEditData3.getText().toString());

            mLoader = getSupportLoaderManager().getLoader(12);
            if (mLoader == null) {
                mLoader = getSupportLoaderManager().initLoader(12, args, this);
            } else {
                mLoader = getSupportLoaderManager().restartLoader(12, args, this);
            }

        } else if (action.equals(ACTION_INSERT)) {

            ContentValues contentValues = new ContentValues();
            contentValues.put("id", 0);
            contentValues.put("name", "new Name");
            contentValues.put("release", "tomorrow");
            getContentResolver().update(Uri.parse("content://com.elegion.roomdatabase.musicprovider/" + table + "/" + ), contentValues, null, null);

        } else if (action.equals(ACTION_UPDATE)) {

        } else if (action.equals(ACTION_DELETE)) {

        } else {
            showToast("Unknown error!");
        }




       /* ContentValues contentValues = new ContentValues();
        contentValues.put("id", 0);
        contentValues.put("name", "new Name");
        contentValues.put("release", "tomorrow");
        getContentResolver().update(Uri.parse("content://com.elegion.roomdatabase.musicprovider/album/1"), contentValues, null, null);*/
    }

    void setupUi() {

        mTableSpinner = findViewById(R.id.spinner_table);
        String[] tablelabels =  getResources().getStringArray(R.array.tablelabels);
        SpinnerAdapter spinnerTablesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tablelabels);
        mTableSpinner.setAdapter(spinnerTablesAdapter);
        mTableSpinner.setSelection(0);

        mActionSpinner = findViewById(R.id.spinner_action);
        String[] actions =  getResources().getStringArray(R.array.actions);
        SpinnerAdapter spinnerActionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, actions);
        mActionSpinner.setAdapter(spinnerActionsAdapter);
        mActionSpinner.setSelection(0);


        mEditId = findViewById(R.id.edit_id);
        mEditData1 = findViewById(R.id.edit_data1);
        mEditData2 = findViewById(R.id.edit_data2);
        mEditData3 = findViewById(R.id.edit_data3);

        findViewById(R.id.btn_action).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // data url
        String table = args.getString("TABLE");
        if (table.isEmpty()) {
            showToast("Table selection error!");
        }

        String url = "content://com.elegion.roomdatabase.musicprovider/" + table.toLowerCase();
        // data id
        Uri contentUri = Uri.parse(url);
        Uri uri = contentUri;
        String dataId = args.getString("ID");


        if (!dataId.isEmpty()) {
           uri = ContentUris.withAppendedId(contentUri,Long.parseLong(dataId));
        }

        return new CursorLoader(this,
                uri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            String[] columnNames = data.getColumnNames();
            StringBuilder builder = new StringBuilder();
            do {
                for (String columnName : columnNames) {
                    builder.append(data.getString(data.getColumnIndex(columnName))).append(";");
                }
                builder.append("\n");
            } while (data.moveToNext());
            showToast(builder.toString());
        } else {
            showToast("Records with that ID do not exist.");
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
