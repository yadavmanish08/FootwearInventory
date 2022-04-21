package com.example.footwearinventory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.footwearinventory.data.inventoryContract;
import com.example.footwearinventory.data.inventoryDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


private static final int STOCK_LOADER =0;
private inventoryDbHelper mDbHelper;

InventoryCursorAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab= findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditorActivity.class);
                startActivity(intent);
            }
        });
       // mDbHelper= new inventoryDbHelper(this);

      final ListView ShoeListView= (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
       ShoeListView.setEmptyView(emptyView);
       // getLoaderManager().initLoader(STOCK_LOADER, null, this);
        LoaderManager.getInstance(this).initLoader(STOCK_LOADER, null, this);

        ShoeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent= new Intent(MainActivity.this,EditorActivity.class);

               Uri currentInventoryUri= ContentUris.withAppendedId(inventoryContract.inventoryEntry.CONTENT_URI,id);
               intent.setData(currentInventoryUri);
               startActivity(intent);

            }
        });


        stockAdapter= new InventoryCursorAdapter(this,null);
        ShoeListView.setAdapter(stockAdapter);

    }
    private void insertStock(){
        ContentValues values= new ContentValues();
        values.put(inventoryContract.inventoryEntry.COL_INVENTORY_NAME," Black Converse");
        values.put(inventoryContract.inventoryEntry.COL_PRICE,2222.00);
        values.put(inventoryContract.inventoryEntry.COL_QUANTITY,10);
        values.put(inventoryContract.inventoryEntry.COL_IMAGE,"android.resource://com.example.footwearinventory/drawable/converse");
        Uri newUri = getContentResolver().insert(inventoryContract.inventoryEntry.CONTENT_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catalog_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.insert_dummy_data:
                insertStock();
                //displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_entries:
               deleteAllStock();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id,  Bundle args) {

        String[] projection = {
                inventoryContract.inventoryEntry._ID,
                inventoryContract.inventoryEntry.COL_INVENTORY_NAME,
                inventoryContract.inventoryEntry.COL_PRICE,
                inventoryContract.inventoryEntry.COL_QUANTITY,
                inventoryContract.inventoryEntry.COL_IMAGE};

                return new CursorLoader(this,
                        inventoryContract.inventoryEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);


    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
    stockAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    stockAdapter.swapCursor(null);
    }

    private void deleteAllStock() {
        int rowsDeleted = getContentResolver().delete(inventoryContract.inventoryEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
}