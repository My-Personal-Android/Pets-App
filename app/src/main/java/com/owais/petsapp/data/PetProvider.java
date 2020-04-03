package com.owais.petsapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/* this is the provider which is used for 3 below reson
* 1 : donot access database directly (validation are done)
* 2 : share data  to other apps to which we give access not all apps
* 3 : to easily use other Android Framework classes like CursorAdapter
*/
public class PetProvider extends ContentProvider {
    //for log messages
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    // object for helper database access
    private PetDbHelper mDbHelper;

    public static final int PETS = 1000;
    public static final int PET_ID = 1001;

    // use to match uri to specific uri so the proper action do on database
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static { // add  the URI here

        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }


    // this below is for initialize the PET_PROVIDER
    public boolean onCreate() {
        mDbHelper = new PetDbHelper(getContext());
        return true;
    }

    // for getting whole data from table OR single pet date
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS: { // to get all table data

                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            }
            case PET_ID: {

                selection = PetContract.PetEntry._ID + "=?";

                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };

                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            }
            default: {
                throw new IllegalArgumentException("Cannot Query Unkown Uri " + uri);
            }
        }

       // this is used to update the uri to inform cursor loader that something has changed and update new data
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS: {
                return insert_pet(uri, contentValues);
            }
            default: {
                throw new IllegalArgumentException("Insertion is not supported for this Uri " + uri);
            }
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {
            case PETS: {

                rowsDeleted = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    // this is used to update the uri to inform cursor loader that something has changed and update new data
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;
            }

            case PET_ID: {

                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                rowsDeleted = database.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);

                if (rowsDeleted != 0) {
                    // this is used to update the uri to inform cursor loader that something has changed and update new data
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsDeleted;

            }
            default: {
                throw new IllegalArgumentException("deletion ios not suppoerted for this delete methos with uri " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS: {

                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            case PET_ID: {

                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePet(uri, contentValues, selection, selectionArgs);
            }
            default: {
                throw new IllegalArgumentException("update is not supported for " + uri);
            }
        }
    }

    private int updatePet(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_NAME)) {
            String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires name ");
            }
        }

        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_GENDER)) {
            Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        if (contentValues.containsKey(PetContract.PetEntry.COLUMN_PET_WEIGHT)) {
            Integer weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    private Uri insert_pet(Uri uri, ContentValues values) {

        String name = values.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);

        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // TODO: Finish sanity checking the rest of the attributes in ContentValues
        Integer gender = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);


        if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }


        Integer weight = values.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new pet with the given values
        long id = database.insert(PetContract.PetEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // this is used to update the uri to inform cursor loader that something has changed and update new data
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

}
