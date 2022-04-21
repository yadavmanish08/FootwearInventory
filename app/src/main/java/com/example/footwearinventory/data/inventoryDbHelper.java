package com.example.footwearinventory.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class inventoryDbHelper extends SQLiteOpenHelper {
   // public static final String LOG_TAG =inventoryDbHelper.class.getSimpleName();

    public static final int DATABASE_VERSION=1;
    private static  final String DATABASE_NAME ="stock.db";
    public inventoryDbHelper(Context context){
        super(context, DATABASE_NAME,null, DATABASE_VERSION);


    }
    @Override
    public  void onCreate(SQLiteDatabase db) {

       final String CREATE_INVENTORY_TABLE = "CREATE TABLE " +
                inventoryContract.inventoryEntry.TABLE_NAME + "(" +
                inventoryContract.inventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                inventoryContract.inventoryEntry.COL_INVENTORY_NAME + " TEXT NOT NULL," +
                inventoryContract.inventoryEntry.COL_PRICE + " INTEGER NOT NULL,"+
                inventoryContract.inventoryEntry.COL_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                inventoryContract.inventoryEntry.COL_IMAGE + " TEXT NOT NULL) ";


               db.execSQL(CREATE_INVENTORY_TABLE);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }




}
