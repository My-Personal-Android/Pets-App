
package com.owais.petsapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// this class represent as a database schema holder for different types of table
// while inside this all the classes are serve as a table schema in a database given by SqliteOpenHelper
public final class PetContract {

    public static final String CONTENT_AUTHORITY = "com.owais.petsapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // this represents as table name
    public static final String PATH_PETS = "pets";


    private PetContract() {
    }

    // here defines the table contents of Pets table
    public static final class PetEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);// "content://com.owais.petsapp/pets"

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


        public final static String TABLE_NAME = "pets";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PET_NAME = "Name";

        public final static String COLUMN_PET_BREED = "Breed";

        public final static String COLUMN_PET_GENDER = "Gender";

        public final static String COLUMN_PET_WEIGHT = "Weight";


        public static final int GENDER_UNKNOWN = 0;

        public static final int GENDER_MALE = 1;

        public static final int GENDER_FEMALE = 2;

        public static boolean isValidGender(int gender) {

            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }
    }

}

