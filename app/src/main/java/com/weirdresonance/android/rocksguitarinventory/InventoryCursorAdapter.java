package com.weirdresonance.android.rocksguitarinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.InventoryEntry;

import static com.weirdresonance.android.rocksguitarinventory.R.id.product_image;


/**
 * Created by Stephen.Pierce on 14/07/2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        ImageView pictureImageView = (ImageView) view.findViewById(product_image);
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        Button sell = (Button) view.findViewById(R.id.sell_button);

        int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);
        final int currentId = cursor.getInt(idColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);


        quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        final int inventoryQuantity = cursor.getInt(quantityColumnIndex);
        //final int quantityID = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentId);




                ContentValues values = new ContentValues();



                if (inventoryQuantity > 0){
                    int newQuantity = inventoryQuantity - 1;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    Uri newUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentId);
                    context.getContentResolver().update(
                            newUri,
                            values ,
                            null,
                            null
                    );
                }else {
                    Toast.makeText(context, context.getString(R.string.out_of_stock),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
