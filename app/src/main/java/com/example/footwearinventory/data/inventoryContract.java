package com.example.footwearinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class inventoryContract{
    private inventoryContract(){}
    
    public static final String CONTENT_AUTHORITY ="com.example.footwearinventory";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY ="inventory";
    
    public static class inventoryEntry implements BaseColumns{

        public static final Uri  CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_INVENTORY);
        
        public static final String  CONTENT_LIST_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;
        
        public static final String TABLE_NAME="inventory";
        public static final String  _ID= BaseColumns._ID;
        public static final String COL_INVENTORY_NAME= "name";
        public static final String COL_PRICE="price";
        public static final String COL_QUANTITY="quantity";
        public static final String COL_IMAGE= "image";





    }


}
