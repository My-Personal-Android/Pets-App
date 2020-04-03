
package com.owais.petsapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.owais.petsapp.data.PetContract.PetEntry;


public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int Pet_Loader_ID = 0;

    PetCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView Pet_listView = (ListView) findViewById(R.id.list_items);

        View emptyview = findViewById(R.id.empty_view); // find the relative layout of id empty_view
        Pet_listView.setEmptyView(emptyview); // set the listview to empty layout if not data is in the app database


        cursorAdapter = new PetCursorAdapter(this, null);
        Pet_listView.setAdapter(cursorAdapter);


        Pet_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(Pet_Loader_ID, null, this);
    }


    // for dummy data purposes
    private void insertPet() {

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 12);

        Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(this, "Error while saving pet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pet saved Successfully", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_insert_dummy_data:

                insertPet();

                return true;

            case R.id.action_delete_all_entries:

                deleteAllPets();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) { // runs on backgroun thread to load all data when new object is created

        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED};

// the new class like AsyncTaskLoader not created because it is concrete class not abstract like AsyncTaskLoader
        return new CursorLoader(this,
                PetEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
