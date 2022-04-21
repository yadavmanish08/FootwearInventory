package com.example.footwearinventory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.footwearinventory.data.inventoryContract.inventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity catalogActivity;

    public InventoryCursorAdapter(MainActivity context, Cursor c){

        super(context,c,0);
        this.catalogActivity = context;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView =(TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView =(TextView) view.findViewById(R.id.quantity);
        ImageView imageImageView = (ImageView) view.findViewById(R.id.edit_image);

        int idColumnIndex =cursor.getColumnIndex(inventoryEntry._ID);
        int nameColumnIndex= cursor.getColumnIndex(inventoryEntry.COL_INVENTORY_NAME);
        int priceColumnIndex= cursor.getColumnIndex(inventoryEntry.COL_PRICE);
        int quantityColumnIndex= cursor.getColumnIndex(inventoryEntry.COL_QUANTITY);
        int imageColumnIndex= cursor.getColumnIndex(inventoryEntry.COL_IMAGE);

        final int inventoryId= cursor.getInt(idColumnIndex);
        String inventoryUnitName =cursor.getString(nameColumnIndex);
        float inventoryPrice = cursor.getFloat(priceColumnIndex);
        final int stockQuantity = cursor.getInt(quantityColumnIndex);
        String stockImageUri= cursor.getString(imageColumnIndex);

        nameTextView.setText(inventoryUnitName);
        priceTextView.setText(String.valueOf(inventoryPrice));
        quantityTextView.setText(String.valueOf(stockQuantity));

        if (!TextUtils.equals(stockImageUri, catalogActivity.getString(R.string.no_image))) {
            imageImageView.setImageURI(Uri.parse(stockImageUri));
        } else {
            imageImageView.setImageURI(Uri.parse(catalogActivity.getString(R.string.no_image_url)));
        }
    }
}
