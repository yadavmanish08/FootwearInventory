package com.example.footwearinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.footwearinventory.data.inventoryContract.inventoryEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class inventoryprovider extends ContentProvider {

    private static final String LOG_TAG = inventoryprovider.class.getSimpleName();
    private inventoryDbHelper mDbHelper;

    private static final int INVENTORY=100;
    private static final int INVENTORY_ID= 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static  {
        sUriMatcher.addURI(inventoryContract.CONTENT_AUTHORITY, inventoryContract.PATH_INVENTORY,INVENTORY);
        sUriMatcher.addURI(inventoryContract.CONTENT_AUTHORITY,inventoryContract.PATH_INVENTORY+"/#", INVENTORY_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new inventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database= mDbHelper.getReadableDatabase();
        //return null;
        Cursor cursor;
        int match= sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                cursor= database.query(inventoryEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,null);
                break;

            case INVENTORY_ID:
                selection = inventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the stock table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(inventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match= sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return inventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return inventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match= sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return insertInventory(uri,contentValues);
            default:
                throw new IllegalArgumentException("insertion is not supported for "+uri);
        }

    }
        private Uri insertInventory(Uri uri, ContentValues values) {
            String name = values.getAsString(inventoryEntry.COL_INVENTORY_NAME);
            if(name== null){
                throw new IllegalArgumentException("inventory requires a name");
            }
            float price= values.getAsInteger(inventoryContract.inventoryEntry.COL_PRICE);
            if( price < 0){
                throw new IllegalArgumentException("inventory requires a valid price");

            }

            int quantity =values.getAsInteger(inventoryEntry.COL_QUANTITY);
            if(quantity < 0 ){
                throw new IllegalArgumentException("not valid quantity");
            }
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            long id= database.insert(inventoryEntry.TABLE_NAME,null,values);


            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }
           getContext().getContentResolver().notifyChange(uri, null);


            return ContentUris.withAppendedId(uri, id);
        }






    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
       SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match= sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                rowsDeleted= database.delete(inventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case INVENTORY_ID:
                selection= inventoryEntry._ID + "=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted=database.delete(inventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new  IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = inventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }


    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(inventoryEntry.COL_INVENTORY_NAME)) {
            String name= values.getAsString(inventoryEntry.COL_INVENTORY_NAME);
            if(name== null){
                throw new IllegalArgumentException("requires name");
            }


        }

        if(values.containsKey(inventoryEntry.COL_PRICE)){
            float price= values.getAsInteger(inventoryEntry.COL_PRICE);
            if( price < 0){
                throw new IllegalArgumentException("inventory requires a valid price");

            }
        }

        if(values.containsKey(inventoryEntry.COL_QUANTITY)){
            int quantity =values.getAsInteger(inventoryEntry.COL_QUANTITY);
            if(quantity < 0 ){
                throw new IllegalArgumentException("not valid quantity");
            }
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
     int rowsUpdated= database.update(inventoryEntry.TABLE_NAME,values,selection,selectionArgs);
     if(rowsUpdated!=0){
         getContext().getContentResolver().notifyChange(uri,null);
     }
     return rowsUpdated;

    }



}
