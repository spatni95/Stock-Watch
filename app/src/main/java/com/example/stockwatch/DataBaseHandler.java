package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "StockAppDB";

    // DB Table Name
    private static final String TABLE_NAME = "StockTable";

    ///DB Columns
    private static final String STOCK_SYMBOL = "StockSymbol";
    private static final String STOCK_NAME = "StockName";
    private static final String STOCK_PRICE = "StockPrice";
    private static final String STOCK_CHANGE = "StockChange";
    private static final String STOCK_CHANGE_PERCENTAGE = "StockChangePercentage";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    STOCK_SYMBOL + " TEXT not null unique," +
                    STOCK_NAME + " TEXT not null)";

    private SQLiteDatabase database;

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); //Setting Reference to the database for further communication.
        Log.d(TAG, "DataBaseHandler: Creation of Database Done");
    }

    //This method is called automatically, only if database doesn't exist, like for 1st time.
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Creating Database");
        db.execSQL(SQL_CREATE_TABLE); //This statement will execute anything in SQL.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //To Load the data from Database
    public ArrayList<String[]> loadStocks() {
        Log.d(TAG, "loadStocks: Start Loading");
        ArrayList<String[]> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,
                new String[]{STOCK_SYMBOL, STOCK_NAME},
                null,
                null,
                null,
                null,
                STOCK_SYMBOL);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                //Double price = cursor.getDouble(1);
               // Double change = cursor.getDouble(2);
                //Double changePer = cursor.getDouble(3);
                String name = cursor.getString(1);
                stocks.add(new String[]{symbol, name});
                cursor.moveToNext();
            }
            cursor.close();
        }
    return stocks;
}

    public void addStock(Stocks stock){
        ContentValues values = new ContentValues(); //Fancy Data Holder
        values.put(STOCK_SYMBOL, stock.getStock_symbol()); //Putting values in particular columns
        //values.put(STOCK_PRICE, stock.getStock_latestPrice());
        //values.put(STOCK_CHANGE, stock.getStock_change());
        //values.put(STOCK_CHANGE_PERCENTAGE, stock.getStock_changePercent());
        values.put(STOCK_NAME, stock.getStock_companyName());
        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addCountry: " + key);

    }

    public void findStock(Stocks stock){

    }


    //Deleting Particular Stock from database
    public void deleteStock(String name){
        Log.d(TAG, "deleteStock: "+name);
        int cnt = database.delete(TABLE_NAME, STOCK_NAME + " = ?", new String[]{name});
        Log.d(TAG, "deleteStock: "+cnt);
    }


    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: ---------------------------------------------");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                //Double price = cursor.getDouble(1);
                //Double change = cursor.getDouble(2);
                //Double changePer = cursor.getDouble(3);
                String name = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", STOCK_SYMBOL + ":", symbol) +
                        //String.format("%s %-18s", STOCK_PRICE + ":",  price) +
                        //String.format("%s %-18s", STOCK_CHANGE + ":", change) +
                        //String.format("%s %-18s", STOCK_CHANGE_PERCENTAGE + ":", changePer) +
                        String.format("%s %-18s", STOCK_NAME + ":", name));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: -----------------------------------------------------");
    }

    public void shutDown() {
        database.close();
    }
}
