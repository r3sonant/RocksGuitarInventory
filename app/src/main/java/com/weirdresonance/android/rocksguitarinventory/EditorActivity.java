package com.weirdresonance.android.rocksguitarinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.weirdresonance.android.rocksguitarinventory.data.InventoryContract.InventoryEntry;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.weirdresonance.android.rocksguitarinventory.R.id.incDecLayout;
import static com.weirdresonance.android.rocksguitarinventory.R.id.orderMoreLayout;

/**
 * Created by Steev on 06/07/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * Data loader identifier for the product.
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    /**
     * Content URI for an existing product. This will be null if a new product.
     */
    private Uri mCurrentProductUri;
    /**
     * Product name EditText field.
     */
    private EditText mNameEditText;
    /**
     * Price EditText field.
     */
    private EditText mPriceEditText;
    /**
     * Quantity EditText field.
     */
    private EditText mQuantityEditText;
    /**
     * Supplier EditText field.
     */
    private EditText mSupplierEditText;
    /**
     * Supplier Email Edit
     */
    private EditText mSupplierEmailEditText;

    // TODO: Need to add the component to take and add the photo.
    /**
     * Increase multiplier
     */
    private EditText mStockMultiplier;
    /**
     * Flag to track if a change has been made.
     */
    private boolean mProductHasChanged = false;
    private String mCurrentPhotoPath;


    /**
     * Listener to detect if the user has interacted with the view so we know they are modifying something.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // See which intent called the EditorActivity and change the title accordingly.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        // Get the views to be hidden ID's.
        View incdDecLayout = (findViewById(incDecLayout));
        View orderMoreButton = (findViewById(orderMoreLayout));

        // If the intent was called by clicking the Add Product button then show New Product as the title.
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.new_product));

            // Hide the decrease, increase and order buttons as they aren't needed when creating a new product.
            incdDecLayout.setVisibility(View.GONE);
            orderMoreButton.setVisibility(View.GONE);

            // Hide the options menu. It will be redrawn later to show the done / save tick.
            invalidateOptionsMenu();
        } else {
            // Otherwise show Edit Product as the user has clicked on a product in the list.
            setTitle(getString(R.string.edit_product));

            // Make the buttonPanel visible.
            incdDecLayout.setVisibility(View.VISIBLE);
            orderMoreButton.setVisibility(View.VISIBLE);

            // Initialize a loader to read from the database and display.
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Get all the views from the EditorActivity
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mSupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mSupplierEmailEditText = (EditText) findViewById(R.id.edit_product_supplier_email);
        mStockMultiplier = (EditText) findViewById(R.id.multiplyer);

        // Set OnTouchListeners on all the fields so we know when the user interacts with them.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);

/*        String stringMultiplier = mStockMultiplier.getText().toString();
        final int multiplier = Integer.valueOf(stringMultiplier);*/

        // Get the decrease button and then set an onclicklistener on it.
        ImageButton decrease = (ImageButton) findViewById(R.id.decrease);

        decrease.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        // Get the Increase button and then set an onclicklistener on it.
        ImageButton increase = (ImageButton) findViewById(R.id.increase);

        increase.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        // Get the picture button and then set an onclicklistener on it.
        final ImageButton takePicture = (ImageButton) findViewById(R.id.takePicture);

        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();


            }
        });

        // Get the Order button and then set an onlclicklistener on it.
        Button order = (Button) findViewById(R.id.order);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();
            }
        });

/*        takePictureButton = (Button) findViewById(R.id.button_image);
        imageView = (ImageView) findViewById(R.id.imageview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            takePictureButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    // Called to hide and show some menu options depending on the acivity
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If new product hide delet option
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    // What to do when an option is selected in the menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Validate entries
                validateEntries();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up delete confirmation dialogue.
                showDeleteConfirmationDialog();
                return true;
            // Go back if up pressed.
            case android.R.id.home:
                // If no changes then go up.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // If there were changes then pop up a dialogue for discard or keep editing
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Discard tapped, go back to InventoryActivity.
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // Show unsaved changes dialogue.
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Validates the entries entered in for the new item. Also validates if email address is of a valid format.
    private void validateEntries() {
        // Read from input fields and trim input.
        String pictureString = "picture";
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (mCurrentProductUri == null &&
                (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                        TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString)
                        || TextUtils.isEmpty(supplierEmailString) || (!supplierEmailString.matches(emailPattern)))) {
            // Warn of unsaved changes.
            DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
            };

            fillAllFieldsWarning(discardButtonClickListener);

            return;
        } else {
            // Save the product in the DB.
            saveProduct();
            finish();
            return;
        }

    }

    /**
     * Called on up button tap.
     */
    @Override
    public void onBackPressed() {
        // If no change then proceed.
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Warn of changes and prompt for keep editing or discard.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Get the input and save to the DB.
     */
    private void saveProduct() {
        // Read the input fields and trim them.
        String pictureString = "picture";
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        // If all fields are empty return.
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierString) && TextUtils.isEmpty(supplierEmailString)) {
            return;
        }

        if (mCurrentProductUri == null &&
                (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                        TextUtils.isEmpty(quantityString) || TextUtils.isEmpty(supplierString)
                        || TextUtils.isEmpty(supplierEmailString))) {
            return;
        }

        // Create content objects.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_PICTURE, pictureString);
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);

        // Is this a new or existing product?
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show toast if successful or failed.
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_product_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_product_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // If not new then update the product.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            // Show successful or failed toast.
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_product_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_product_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Projection for all columns
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in Cursor
                null,                   // No clause
                null,                   // No arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Move to first row of cursor and get the date from the columns we require.
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierEmailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);

            // Extract the value from the Cursor column index
            String name = cursor.getString(nameColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            // Update views on the screen with values from DB.
            mNameEditText.setText(name);
            mPriceEditText.setText(Float.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierEditText.setText(supplier);
            mSupplierEmailEditText.setText(supplierEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear all data from input fields
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierEditText.setText("");
        mSupplierEmailEditText.setText("");
    }

    // Alert for unsaved changes with discard or keep editing.
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Prompt for deltion
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Delete the product from the DB.
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            // Show sucessfull or failed delete.
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_product_deletion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_product_deletion_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    // Decrease the quantity if the decrease quantity button is pressed.
    public void decreaseQuantity() {
        String stringMultiplier = mStockMultiplier.getText().toString();
        int multiplier = Integer.valueOf(stringMultiplier);
        String stringQuantity = mQuantityEditText.getText().toString();
        int quantity = Integer.valueOf(stringQuantity);
        if (mCurrentProductUri != null && quantity > 0 && multiplier > 0) {

            quantity = quantity - multiplier;
            if (quantity < 1) {
                quantity = 0;
            }
            stringQuantity = Integer.toString(quantity);
            mQuantityEditText.setText(stringQuantity);
        }
    }

    // Increase the quantity if the increase button is pressed.
    private void increaseQuantity() {
        String stringMultiplier = mStockMultiplier.getText().toString();
        int multiplier = Integer.valueOf(stringMultiplier);
        String stringQuantity = mQuantityEditText.getText().toString();
        int quantity = Integer.valueOf(stringQuantity);
        if (mCurrentProductUri != null && multiplier > 0) {

            quantity = quantity + multiplier;

            stringQuantity = Integer.toString(quantity);
            mQuantityEditText.setText(stringQuantity);
        }
    }





/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                takePictureButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);

        startActivityForResult(intent, 100);
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setImageURI(file);
            }
        }
    }*/


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "rgi_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,   //prefix
                ".jpg",          //suffix
                storageDir       //directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

/*    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "rgi_" + timeStamp + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(),  imageFileName);
        return photo;
    }*/

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(LOG_TAG, "Failed to save image " + ex.toString());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.weirdresonance.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ImageView pictureImage = (ImageView) findViewById(R.id.product_image);
            pictureImage.setImageBitmap(imageBitmap);
        }
    }*/


    // Create an intent and open an email client on the device passing in values from the DB for
    // the order.
    public void submitOrder() {

        // If the product hasn't changed then create the email.
        if (!mProductHasChanged) {
            String productName = mNameEditText.getText().toString();
            String supplierName = mSupplierEditText.getText().toString();
            String supplierEmail = mSupplierEmailEditText.getText().toString();

            // Create the email content
            String email = "mailto:"
                    + supplierEmail
                    + "?subject="
                    + Uri.encode("New product order from Rocks Guitars for "
                    + productName)
                    + "&body="
                    + Uri.encode("Rocks Guitars would like to place an order for more of the following product."
                    + "\n\n"
                    + "Product: "
                    + productName
                    + "\n\n"
                    + "Quantity: (Please fill in)"
                    + "\n\n"
                    + "Thank you "
                    + supplierName
                    + "."
                    + "\n\n"
                    + "Rocks Guitars (orderrequest@rocksguitars.com)");

            // Create Intent to send email
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("message/rfc822");
            intent.setData(Uri.parse(email)); // Only email apps should handle this.

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } else {
            // If the product has changed prompt the user to keep editing or discard their changes.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };

            // Show dialog that there are unsaved changes.
            showFieldsChangedOrderMore(discardButtonClickListener);
        }
    }

    // Warn if unsaved changes.
    private void showFieldsChangedOrderMore(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Changes have been made to the product. Please save before ordering more.");
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Warn the user they must fill in all fields before proceeding.
    private void fillAllFieldsWarning(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill in all fields and enter a valid email address before saving.");
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
