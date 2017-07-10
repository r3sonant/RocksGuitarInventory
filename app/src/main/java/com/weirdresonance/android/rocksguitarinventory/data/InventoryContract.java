package com.weirdresonance.android.rocksguitarinventory.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Rocks Guitar Inventory app.
 */
public final class InventoryContract {

    // Empty constructor so the InventoryContract class can't be instantiated.
    private InventoryContract() {
    }

    /**
     * Content Authority for the content provider.
     */
    public static final String CONTENT_AUTHORITY = "com.weirdresonance.android.rocksguitarinventory";

    /**
     * Use the CONTENT_AUTHORITY to construct the base for all URI's the app will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path to be appended to base URI.
     */
    public static final String PATH_INVENTORY = "inventory";

    /**
     * Inner class to define all the constant values that will be used in the Inventory DB table.
     */
    public static final class InventoryEntry implements BaseColumns {

        // Content URI for accessing the product date in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        // Create the MIME type of the CONTENT_URI for the list of products.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // Create the MIME type of the CONTENT_URI for a single product.
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        // The name of the database table for storing the products in.
        public static final String TABLE_NAME = "inventory";

        /**
         * Unique ID number for each product in the DB.
         * <p>
         * Type: INTEGER
          */
        public static final String _ID = BaseColumns._ID;

        /**
         * Image for inventory item.
         * </p>
         * Type: String
         */
        public static final String COLUMN_ITEM_PICTURE = "picture";

        /**
         * Name of the item.
         * <p>
         * Type: String
         */
        public static final String COLUMN_ITEM_NAME = "name";

        /**
         * Price of the item.
         * <p>
         * Type: Float
         */
        public static final String  COLUMN_ITEM_PRICE = "price";

        /**
         * Quantity of items.
         * <p>
         * Type: Int
         */
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
    }

    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int PETS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table
     */
    private static final int PET_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.pets/pets" will map to the
        // integer code {@link #PETS}. This URI is used to provide access to MULTIPLE rows
        // of the pets table.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);

        // The content URI of the form "content://com.example.android.pets/pets/#" will map to the
        // integer code {@link #PET_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.pets/pets/3" matches, but
        // "content://com.example.android.pets/pets" (without a number at the end) doesn't match.
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

}
