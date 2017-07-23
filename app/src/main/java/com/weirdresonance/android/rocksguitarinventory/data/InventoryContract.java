package com.weirdresonance.android.rocksguitarinventory.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Rocks Guitar Inventory app.
 */
public final class InventoryContract {

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
     * URI matcher code for the content URI for the inventory table
     */
    private static final int PRODUCTS = 100;
    /**
     * URI matcher code for the content URI for a single product in the inventory table
     */
    private static final int PRODUCT_ID = 101;
    /**
     * UriMatcher object to match the content URI to a corresponding code.
     * The input that is passed into the constructor determines what is returned for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // URI matcher. Corresponding code is returned when a match is found. Applies to whole table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, PRODUCTS);

        // URI matcher for single product in table.
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", PRODUCT_ID);
    }

    // Empty constructor so the InventoryContract class can't be instantiated.
    private InventoryContract() {
    }

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
        public static final String COLUMN_PRODUCT_PICTURE = "picture";

        /**
         * Name of the item.
         * <p>
         * Type: String
         */
        public static final String COLUMN_PRODUCT_NAME = "name";

        /**
         * Price of the item.
         * <p>
         * Type: Float
         */
        public static final String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Quantity of items.
         * <p>
         * Type: Int
         */
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Supplier of items.
         * <p>
         * Type: String
         */
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";

        /**
         * Email address of the product supplier.
         * <p>
         * Type: String
         */
        public static final String COLUMN_PRODUCT_SUPPLIER_EMAIL = "email";
    }
}
