package com.example.footwearinventory;

import android.app.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import com.example.footwearinventory.data.inventoryContract;

import java.io.File;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int STOCK_LOADER = 0;
    Button idSelectImageButton;
      ImageView idImageView;




    private String mCurrentPhotoUri ="no images";

    public static final int PICK_PHOTO_REQUEST = 20;
    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;


    private Uri mCurrentInventoryUri;

     TextView mNameEditText;
     TextView mPriceEditText;
     TextView mQuantityEditText;

    private boolean mInventoryHasChanged = false;
   private View.OnTouchListener mTouchListener =new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent){
         mInventoryHasChanged =true;
         return false;
      }
   };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

       // ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);

      //  getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent= getIntent();
        mCurrentInventoryUri= intent.getData();
        if(mCurrentInventoryUri == null){
            setTitle(getString(R.string.detail_activity_title_new_stock_unit));
         invalidateOptionsMenu();
        }
        else{
            setTitle(getString(R.string.editor_activity_title_edit_inventory));
            LoaderManager.getInstance(this).initLoader(STOCK_LOADER, null, this);
        }





        mNameEditText= (EditText) findViewById(R.id.edit_shoe_name);
        mPriceEditText= (EditText)findViewById(R.id.edit_shoe_price);
        mQuantityEditText= (EditText)findViewById(R.id.edit_shoe_quantity);

        idSelectImageButton=findViewById(R.id.idSelectImageButton);
        idImageView= findViewById(R.id.idImageView);


       mNameEditText.setOnTouchListener(mTouchListener);
       mPriceEditText.setOnTouchListener(mTouchListener);
      mQuantityEditText.setOnTouchListener(mTouchListener);


        
       //idSelectImageButton.setOnClickListener(v -> openGallery());

        idSelectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhotoProductUpdate(v);
            }
        });


    }




    private void saveInventory(){

        String nameString = mNameEditText.getText().toString().trim();
        String priceString =mPriceEditText.getText().toString().trim();
        String quantityString= mQuantityEditText.getText().toString().trim();

        if(mCurrentInventoryUri ==null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString)&&
        TextUtils.isEmpty(quantityString)){
            return;
        }


        ContentValues values = new ContentValues();
        values.put(inventoryContract.inventoryEntry.COL_INVENTORY_NAME,nameString);
        //values.put(inventoryContract.inventoryEntry.COL_PRICE,priceString);

       values.put(inventoryContract.inventoryEntry.COL_IMAGE,mCurrentPhotoUri);

        int quantity= 0;
        if(!TextUtils.isEmpty(quantityString)){
            quantity=Integer.parseInt(quantityString);
        }
        values.put(inventoryContract.inventoryEntry.COL_QUANTITY,quantity);

         float price=0;
        if(!TextUtils.isEmpty(priceString)){
        price= Float.parseFloat(priceString);

        }


        values.put(inventoryContract.inventoryEntry.COL_PRICE, price);

       if(mCurrentInventoryUri == null){

        Uri newUri = getContentResolver().insert(inventoryContract.inventoryEntry.CONTENT_URI,values);

        if(newUri == null){
            Toast.makeText(this, getString(R.string.editor_insert_stock_failed),
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.editor_insert_stock_successful),
                    Toast.LENGTH_SHORT).show();
            finish();

        }
       } else {

           int rowsAffected= getContentResolver().update(mCurrentInventoryUri,values,null,null);

           if (rowsAffected == 0) {
               // If no rows were affected, then there was an error with the update.
               Toast.makeText(this, getString(R.string.editor_update_stock_failed),
                       Toast.LENGTH_SHORT).show();
           } else {
               // Otherwise, the update was successful and we can display a toast.
               Toast.makeText(this, getString(R.string.editor_update_stock_successful),
                       Toast.LENGTH_SHORT).show();
               finish();

           }

       }


    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.del_inventory);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // save pet to database
               // savePet();
                saveInventory();
                //Exit activity and switch back to catalog activity
                break;

            // Respond to a click on the "Delete" menu option
            case R.id.del_inventory:
                // Do nothing for now
                showDeleteConfirmationDialog();
               break;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;



        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


  /**  private void openGallery() {
        Intent gallery= new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
             imageUri = data.getData();
            mCurrentPhotoUri= imageUri.toString();

            //mCurrentPhotoUri= imageUri.toString();
            idImageView.setImageURI(Uri.parse(mCurrentPhotoUri));
        }
    }   */


  public void onPhotoProductUpdate(View view) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          //We are on M or above so we need to ask for runtime permissions
          if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
              invokeGetPhoto();
          } else {
              // we are here if we do not all ready have permissions
              String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
              requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
          }
      } else {
          //We are on an older devices so we dont have to ask for runtime permissions
          invokeGetPhoto();
      }
  }

    private void invokeGetPhoto() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We got a GO from the user
            invokeGetPhoto();
        } else {

            Toast.makeText(this, R.string.err_external_storage_permissions, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                //If we are here, everything processed successfully and we have an Uri data
                Uri mProductPhotoUri = data.getData();
                mCurrentPhotoUri = mProductPhotoUri.toString();
                //Log.d(TAG, "Selected images " + mProductPhotoUri);

                idImageView.setImageURI(Uri.parse(mCurrentPhotoUri));
            }
        }
    }



    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id,  Bundle args) {
        String[] projection={
                inventoryContract.inventoryEntry._ID,
                inventoryContract.inventoryEntry.COL_INVENTORY_NAME,
                inventoryContract.inventoryEntry.COL_PRICE,
                inventoryContract.inventoryEntry.COL_QUANTITY,
                inventoryContract.inventoryEntry.COL_IMAGE,};

                return new CursorLoader(this,
                        mCurrentInventoryUri,
                        projection,
                        null,
                        null,
                        null);



    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


            if (cursor.moveToFirst()) {

                int nameColumnIndex = cursor.getColumnIndex(inventoryContract.inventoryEntry.COL_INVENTORY_NAME);
                int priceColumnIndex = cursor.getColumnIndex(inventoryContract.inventoryEntry.COL_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(inventoryContract.inventoryEntry.COL_QUANTITY);
                int imageColumnIndex = cursor.getColumnIndex(inventoryContract.inventoryEntry.COL_IMAGE);

                // final int inventoryId= cursor.getInt(idColumnIndex);
                String mInventoryUnitName = cursor.getString(nameColumnIndex);
                float mInventoryPrice = cursor.getFloat(priceColumnIndex);
                int mStockQuantity = cursor.getInt(quantityColumnIndex);
                String stock_image = cursor.getString(imageColumnIndex);
                mCurrentPhotoUri = stock_image;

                mNameEditText.setText(mInventoryUnitName);
                mPriceEditText.setText(String.valueOf(mInventoryPrice));
                mQuantityEditText.setText(String.valueOf(mStockQuantity));


                if (TextUtils.equals(stock_image, getString(R.string.no_image))) {
                    idImageView.setImageURI(Uri.parse(getString(R.string.no_image_url)));
                } else {
                    idImageView.setImageURI(Uri.parse(stock_image));
                }


            }



    }

    @Override
    public void onLoaderReset( Loader<Cursor> loader) {


        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");


    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the stock unit.
                deleteStock();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the stock unit.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteStock() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentInventoryUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_stock_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}