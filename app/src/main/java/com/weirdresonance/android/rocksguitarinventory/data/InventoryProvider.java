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

    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
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
