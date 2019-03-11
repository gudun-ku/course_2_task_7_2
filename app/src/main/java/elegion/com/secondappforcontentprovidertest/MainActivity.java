package elegion.com.secondappforcontentprovidertest;

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

        //let's prepare our arguments
        String[] tables =  getResources().getStringArray(R.array.tables);
        String table = tables[(int) mTableSpinner.getSelectedItemId()];
        Bundle args = new Bundle();
        args.putString("ACTION", mActionSpinner.getSelectedItem().toString());
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
        String dataId = args.getString("ID");
        String selection = null;
        String[] selectionArgs = null;

        if (!dataId.isEmpty()) {
            selection = "id = " + dataId;
            selectionArgs = new String[]{"id"};
        };



        return new CursorLoader(this,
                Uri.parse(url),
                null,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            StringBuilder builder = new StringBuilder();
            do {
                builder.append(data.getString(data.getColumnIndex("id"))).append("\n");
            } while (data.moveToNext());
            Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
