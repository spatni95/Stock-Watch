package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "MainActivity";

    private final ArrayList<Stocks> mySelectedList =  new ArrayList<>();
    private RecyclerView stockRecycler;
    private SwipeRefreshLayout swiper;
    private static final int ADD_CODE = 1;
    private static final int FIND_CODE = 2;
    private DataBaseHandler dataBaseHandler;
    private StocksAdapter stocksAdapter;
    private final ArrayList<String[]> Stock_List = new ArrayList<>();
    private HashMap<String, String> stored_stock = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stockRecycler = findViewById(R.id.recycler);
        stocksAdapter = new StocksAdapter(mySelectedList, this);
        stockRecycler.setAdapter(stocksAdapter);
        stockRecycler.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);
        dataBaseHandler = new DataBaseHandler(this);
        if (doNetCheck()) {
            Log.d(TAG, "onCreate: I am here");
            new StockDownload(this).execute();
        } else {
            Log.d(TAG, "onCreate: No Connection");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (doNetCheck() == true) {
                    loadDataDB();
                }
                else {
                    alertdialog();
                    swiper.setRefreshing(false);
                }
            }
        });
    }
    //Alert for Swipe Refresh
    public void alertdialog(){
        Log.d(TAG, "alertdialog: No Refresh");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Stocks Cannot Be Refreshed Without A Network Connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Checking the Network Connectivity
    public boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnected()) {
            Log.d(TAG, "doNetCheck: internet");
            return false;
        } else {
            Log.d(TAG, "doNetCheck: Internet1");
            return true;
        }
    }

    @Override
    protected void onResume(){
        dataBaseHandler.dumpDbToLog();
        if (Stock_List.isEmpty()) {
            getDataFromDB();
        } else if (doNetCheck()) {
            loadDataDB();
        }
        super.onResume();
    }

    //Loading Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }
    //Menu Selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stock:
                if (!doNetCheck()) {
                    Log.d(TAG, "onOptionsItemSelected: No Internet");
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("No Network Connection");
                    builder.setMessage("Stocks Cannot Be Added Without A Network Connection");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Log.d(TAG, "onOptionsItemSelected: I am here");
                    if (stored_stock.isEmpty())
                        new StockDownload(this).execute();
                        StockInput();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Which stock You want to add to your Watch List
    private void StockInput() {
        Log.d(TAG, "openDialog: I am at first dialog box");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        input.setGravity(Gravity.CENTER);
        builder.setView(input);
        builder.setTitle("Stock Selection");
        builder.setMessage("Please enter a Stock Symbol:");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        Log.d(TAG, "");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SelectStock(input.getText().toString());
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Add Particular stock to WatchList
    public void SelectStock(String stock_symbol){
        Log.d(TAG, "SelectStock: I am at Second Dialog Box");
        final ArrayList<String> final_Stock = new ArrayList<>();
        for (String key : stored_stock.keySet()) {
            if (key.contains(stock_symbol.trim())) {
                final_Stock.add(key + " - " + stored_stock.get(key));
            }
        }
        if (final_Stock.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Log.d(TAG, "SelectStock: Stock Symbol:" +stock_symbol);
            builder.setTitle("Symbol Not Found: " +stock_symbol.trim());
            builder.setMessage("Data for stock symbol");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else if (final_Stock.size() == 1) {
            alreadyStockPresent(0, final_Stock);
        } else if (final_Stock.size() > 1) {
            Collections.sort(final_Stock);
            CharSequence symChars[] = final_Stock.toArray(new CharSequence[final_Stock.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Make a selection");
            builder.setItems(symChars, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (doNetCheck())
                        alreadyStockPresent(which, final_Stock);
                    else
                        getDataFromDB();
                }
            });
            builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void alreadyStockPresent(int i, ArrayList<String> final_Stock) {
        Log.d(TAG, "alreadyStockPresent: Stock Already Present, show Alert BOX");
        Stocks stocks;
        ArrayList<String> symbolList = new ArrayList<>();
        for (int pos = 0; pos < mySelectedList.size(); pos++) {
            stocks = mySelectedList.get(pos);
            Log.d(TAG, "alreadyStockPresent: " +stocks.getStock_symbol());
            symbolList.add(stocks.getStock_symbol());
        }
        if (symbolList.contains(final_Stock.get(i).split("-")[0].trim())) {
            Log.d(TAG, "alreadyStockPresent: Stock Already Present " +final_Stock.get(i).split("-")[0].trim());
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_warning_black_24dp);
            builder.setTitle("Duplicate Stock");
            builder.setMessage("Stock Symbol '" + final_Stock.get(i).split("-")[0].trim() + "' is already displayed");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            new StocksLoading(MainActivity.this).execute(final_Stock.get(i).split("-")[0].trim());
            loadDataDB();
        }

    }

    public void getDataFromDB() {
        ArrayList<String[]> list_stock = dataBaseHandler.loadStocks();
        Stock_List.clear();
        Stock_List.addAll(list_stock);
        mySelectedList.clear();
        stocksAdapter.notifyDataSetChanged();
        Log.d(TAG, "getDataFromDB: Loading Done" +list_stock.size());
        for (int j = 0; j < list_stock.size(); j++) {
            Log.d(TAG, "getDataFromDB: Loop Value " +j);
            dataBaseHandler.deleteStock(list_stock.get(j)[0]);
        }
        for (int i = 0; i < list_stock.size(); i++) {
            Log.d(TAG, "getDataFromDB: Loop1 Value: " +i);
            new StocksLoading(MainActivity.this).execute(list_stock.get(i)[0].trim(), list_stock.get(i)[1].trim());
        }
    }

    //Loading the Data from Database of Selected Stocks

    public void loadDataDB() {
        ArrayList<String[]> list_stock= dataBaseHandler.loadStocks();
        Log.d(TAG, "loadStocksFromDB: Loading Done" +list_stock.size());
        mySelectedList.clear();
        stocksAdapter.notifyDataSetChanged();
        for (int j = 0; j < list_stock.size(); j++) {
            dataBaseHandler.deleteStock(list_stock.get(j)[0]);
        }
        for (int i = 0; i < list_stock.size(); i++) {
            new StocksLoading(MainActivity.this).execute(list_stock.get(i)[0].trim());
        }
        swiper.setRefreshing(false);
    }


    @Override
    protected void onDestroy() {
        dataBaseHandler.shutDown();
        super.onDestroy();
    }


    //public void doRefresh(){
       // swiper.setRefreshing(false);
       // Toast.makeText(this, "Recycle List is Refreshed", Toast.LENGTH_SHORT).show();
    //}

    public void updateStock(HashMap<String, String> stock_Map) {
        Log.d(TAG, "updateStock: " +stock_Map);
        stored_stock.putAll(stock_Map);
        if (stored_stock != null)
            Log.d(TAG, "updateStock: I am here " + stored_stock.size());
    }

    public void updateStockData(Stocks stocks) {
        Log.d(TAG, "updateStockData: Stock Data: " +stocks);
        mySelectedList.add(stocks);
        dataBaseHandler.addStock(stocks);
        Collections.sort(mySelectedList);
        Log.d(TAG, "updateStockData: I am Here");
        stocksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view1) {
        int position = stockRecycler.getChildLayoutPosition(view1);
        Log.d(TAG, "onClick: Position: " +position);
        Stocks stocks = mySelectedList.get(position);
        Log.d(TAG, "onClick: Selected Stock: " +stocks);
        String marketWatchUrl = "http://www.marketwatch.com/investing/stock/" + stocks.getStock_symbol();
        Log.d(TAG, "Open Detail of Particular Stock " + stocks.getStock_symbol());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketWatchUrl));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view1){
            final int position = stockRecycler.getChildLayoutPosition(view1);
            Stocks stock1 = mySelectedList.get(position);
            Log.d(TAG, "onLongClick: Stock Detail: " +stock1);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_delete_black_24dp);
            builder.setTitle("Delete Stock");
            Log.d(TAG, "onLongClick: SYMBOL: " +mySelectedList.get(position).getStock_symbol());
            builder.setMessage("Delete Stock Symbol " + mySelectedList.get(position).getStock_symbol() + " ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id1) {
                    dataBaseHandler.deleteStock(mySelectedList.get(position).getStock_companyName());
                    mySelectedList.remove(position);
                    stocksAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id1) {
                    dialog.dismiss();
                }
            });
            //builder.setMessage("Delete Stock " + mySelectedList.get(position).getStock_companyName() + "?");
            //builder.setTitle("Delete Stock");
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
}
