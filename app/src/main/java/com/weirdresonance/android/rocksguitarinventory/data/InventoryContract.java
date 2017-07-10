package com.weirdresonance.android.rocksguitarinventory.data;

import android.content.ContentResolver;
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



    }

}
