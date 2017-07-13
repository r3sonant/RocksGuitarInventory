package com.weirdresonance.android.rocksguitarinventory.data;

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

import static android.R.attr.name;
import static android.R.attr.value;
import static com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.CONTENT_AUTHORITY;
import static com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.InventoryEntry;
import static com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.PATH_INVENTORY;


/**
 * Created by Steev on 12/07/2017.
 */

public class InventoryProvider extends ContentProvider {

    /** Log message tag */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /** Declare the database helper object */
    private InventoryDbHelper mDbHelper;

    /** Matcher code for the content URI of the inventory table */
    private static final int PRODUCTS = 10;

    /** Matcher code for the content URI for a single product in the inventory table */
    private static final int PRODUCT_ID = 11;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is only run this class is called.
    static {
        // URI matcher call for inventory table.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY, PRODUCTS);

        // URI matcher for an item in the inventory table.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_INVENTORY + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        //Create and initialize a InventoryDbHelper object to get access to the inventory DB.
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get the database as readable
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Declare cursor to hold the result of the query
        Cursor cursor;

        // Check to see if the URI matcher can match the URI to either of the codes defined
        // in PRODUCTS OR PRODUCT_ID.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Query the whole of the inventory table to return all rows.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // Set the selection to the ID of the row to be queried.
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Query the inventory table for the row that matches the passed in ID.
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Returns the data MIME type of the content URI.
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Insert new product data into the provider if the URI passed in is for PRODUCTS.
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("You cannot insert data with this " + " URI.");
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check String picture isn't null.
        String picture = values.getAsString(InventoryEntry.COLUMN_PRODUCT_PICTURE);
        if (picture == null) {
            throw new IllegalArgumentException("Product requires a picture");
        }

        // Check String name isn't null.
        String name = values.getAsString(InventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a valid name.");
        }

        // Check int price isn't null.
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Product requires a valid price.");
        }

        // Check int quantity isn't null.
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Product requires a valid quantity.");
        }

        // Check String supplier isn't null.
        String supplier = values.getAsString(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Product requires a valid supplier.");
        }


        // TODO: Add some error checking if a value isn't mandatory.
/*        // If the weight is provided, check that it's greater than or equal to 0 kg
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);

        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }*/


        // Get a writable instance of the database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new provided product into the database.
        long id = database.insert(InventoryEntry.TABLE_NAME, null, values);


        // TODO: Make sure the comments make sense.
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the pet content URI.
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended to the end.
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
