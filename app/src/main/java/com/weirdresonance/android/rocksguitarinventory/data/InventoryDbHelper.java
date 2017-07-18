package com.weirdresonance.android.rocksguitarinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.InventoryEntry;

/**
 * Created by Stephen.Pierce on 11/07/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper{

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    /**
     * Name for the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Version of the database. Must be incremented if the db schema is changed.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructor for InventoryDbHelper
     *
     * @param context
     */
    public InventoryDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the SQL statement string that will create the Inventory table.
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_PRODUCT_PICTURE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        // Run the SQL statement and create the table.
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not needed at present as the DB is still on version 1.
    }
}
