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

        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final String productQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        //TODO: https://discussions.udacity.com/t/sell-button-issue/222243/25
        // https://discussions.udacity.com/t/problem-with-sold-button/310478/13
        // https://stackoverflow.com/questions/22442479/custom-cursoradapter-wrong-position/22444284#22444284


        //final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ContentValues values = new ContentValues();

                int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
/*                int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
                int categoryColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_CATEGORY_IMAGE);*/

                int inventoryQuantity = cursor.getInt(quantityColumnIndex);
/*                String inventoryName = cursor.getString(nameColumnIndex);
                String inventoryColour = cursor.getString(colourColumnIndex);
                int inventoryPrice = cursor.getInt(priceColumnIndex);
                String inventoryCategory = cursor.getString(categoryColumnIndex);*/

                if (inventoryQuantity > 0){
                    int newQuantity = inventoryQuantity - 1;
/*                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    values.put(InventoryEntry.COLUMN_PRODUCT_NAME, inventoryName);
                    values.put(InventoryEntry.COLUMN_COLOUR, inventoryColour);
                    values.put(InventoryEntry.COLUMN_PRICE, inventoryPrice);
                    values.put(InventoryEntry.COLUMN_CATEGORY_IMAGE, inventoryCategory);*/
                    Uri newUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, newQuantity);
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







/*                ContentResolver mContentResolver = view.getContext().getContentResolver();

                ContentValues values = new ContentValues();

                int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
                int Quantity = cursor.getInt(quantityColumnIndex);
                if (Quantity > 0){
                    int decrementedQuantity = Quantity --;
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, decrementedQuantity);
                    Uri newUri = mContentResolver.insert(InventoryEntry.CONTENT_URI, values);
                }else {

                    Toast.makeText(context, context.getString(R.string.out_of_stock),
                            Toast.LENGTH_SHORT).show();
                }*/

/*                int quantity = Integer.parseInt(productQuantity);
                //int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
                if (quantity<0){
                    Log.v("ProductCursorAdpter","You cannot have less than 1 product");
                    return;
                }
                quantity--;
                //sellProduct();
                values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);*/
            }
        });
    }
}
