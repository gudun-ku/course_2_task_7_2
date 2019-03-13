package elegion.com.secondappforcontentprovidertest;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
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

    private void doWork() {
        final String ACTION_QUERY = getString(R.string.str_action_query);
        final String ACTION_INSERT = getString(R.string.str_action_insert);
        final String ACTION_UPDATE = getString(R.string.str_action_update);
        final String ACTION_DELETE = getString(R.string.str_action_delete);

        String[] tables =  getResources().getStringArray(R.array.tables);
        String action = mActionSpinner.getSelectedItem().toString();
        String table = tables[(int) mTableSpinner.getSelectedItemId()];
        String strId = mEditId.getText().toString();
        if (!strId.isEmpty()) {
            try {
                Integer dataId = Integer.parseInt(strId);
            } catch (Exception e) {
                showToast("Id is not a number!" );
                return;
            }
        } else {
            if (!action.equals(ACTION_QUERY) && !action.equals(ACTION_INSERT)) {
                showToast("Empty id!");
                return;
            }
        }

        if (action.equals(ACTION_QUERY)) {
            Bundle args = new Bundle();
            args.putString("ACTION", action);
            args.putString("TABLE", table);
            args.putString("ID", strId);

            mLoader = getSupportLoaderManager().getLoader(12);
            if (mLoader == null) {
                mLoader = getSupportLoaderManager().initLoader(12, args, this);
            } else {
                mLoader = getSupportLoaderManager().restartLoader(12, args, this);
            }

        } else if (action.equals(ACTION_INSERT)) {

            ContentValues contentValues = new ContentValues();
            try {
                if (table.equals("album")) {
                    contentValues.put("id", Integer.parseInt(strId));
                    contentValues.put("name", mEditData2.getText().toString());
                    contentValues.put("release", mEditData3.getText().toString());
                } else if (table.equals("song")) {
                    contentValues.put("id", Integer.parseInt(strId));
                    contentValues.put("name", mEditData2.getText().toString());
                    contentValues.put("duration", mEditData3.getText().toString());
                } else if (table.equals("albumsong")) {
                    contentValues.put("song_id", Integer.parseInt(mEditData2.getText().toString()));
                    contentValues.put("album_id", Integer.parseInt(mEditData3.getText().toString()));
                }
            } catch (Exception e) {
                showToast("Wrong data! \n" + e.getMessage() );
                return;
            }

            try {
                getContentResolver().insert(Uri.parse("content://com.elegion.roomdatabase.musicprovider/" + table), contentValues);
            } catch (SQLiteConstraintException se) {
                showToast("Error! That album or song id is wrong and does not exist in tables");
                return;
            } catch (Exception e) {
                showToast("Error! " + e.getLocalizedMessage());
                return;
            }
            showToast("Sucessfully inserted");

        } else if (action.equals(ACTION_UPDATE)) {
            ContentValues contentValues = new ContentValues();
            try {

                if (table.equals("album")) {
                    contentValues.put("id", Integer.parseInt(strId));
                    contentValues.put("name", mEditData2.getText().toString());
                    contentValues.put("release", mEditData3.getText().toString());
                } else if (table.equals("song")) {
                    contentValues.put("id", Integer.parseInt(strId));
                    contentValues.put("name", mEditData2.getText().toString());
                    contentValues.put("duration", mEditData3.getText().toString());
                } else if (table.equals("albumsong")) {
                    contentValues.put("id", Integer.parseInt(strId));
                    contentValues.put("song_id", Integer.parseInt(mEditData2.getText().toString()));
                    contentValues.put("album_id", Integer.parseInt(mEditData3.getText().toString()));
                }
            } catch (Exception e) {
                showToast("Wrong data! \n" + e.getMessage() );
                return;
            }

            int result = 0;
            try {
                result = getContentResolver().update(ContentUris.withAppendedId(Uri.parse("content://com.elegion.roomdatabase.musicprovider/" + table), Long.parseLong(strId)), contentValues, null, null);
            } catch (SQLiteConstraintException se) {
                showToast("Error! That album or song id is wrong and does not exist in tables");
                return;
            } catch (Exception e) {
                showToast("Error! " + e.getLocalizedMessage());
                return;
            }
            showToast("Updated " + result + " records.");
        } else if (action.equals(ACTION_DELETE)) {

            int result = 0;
            try {
                result = getContentResolver().delete(ContentUris.withAppendedId(Uri.parse("content://com.elegion.roomdatabase.musicprovider/" + table),Long.parseLong(strId)), null, null);
            } catch (SQLiteConstraintException se) {
                showToast("Error! That album or song id is wrong and does not exist in tables");
                return;
            } catch (Exception e) {
                showToast("Error! " + e.getLocalizedMessage());
                return;
            }
            showToast("Deleted " + result + " records.");
        } else {
            showToast("Unknown error!");
        }
    }

    @Override
    public void onClick(View v) {
        doWork();
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
