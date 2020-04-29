package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class StocksLoading extends AsyncTask<String, Void, String> {
    private static final String TAG = "StocksLoading";
    private final java.lang.String url1 = "https://cloud.iexapis.com/stable/stock/";
    private final java.lang.String url2 = "/quote?token=pk_1b8d001cd8994d11a409cf44a25592fb";
    private ArrayList<String> StocksData = new ArrayList<>();
    private MainActivity mainActivity;

    public StocksLoading(MainActivity ma){
        mainActivity = ma;
    }
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: String Size" +strings.length);
        if (strings.length == 2) {
            StocksData.add(strings[0]);
            StocksData.add(strings[1]);
        }
        Uri dataUri = Uri.parse(url1 + strings[0] + url2);
        String urlToUse = dataUri.toString();
        StringBuilder stringbuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: Check Connection: " + conn.getResponseCode());
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) {
                stringbuilder.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + stringbuilder.toString());
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Error ", e);
            return null;
        }
        Log.d(TAG, "doInBackground: Data is Here: " + stringbuilder.toString());
        return stringbuilder.toString();
    }

    @Override
    protected void onPostExecute(String str1) {
        if (str1 != null) {
            Stocks stocks = parseJSON(str1);
            Log.d(TAG, "onPostExecute: Stocks:" +stocks);
            mainActivity.updateStockData(stocks);
        } else {
            if (StocksData.size() == 2) {
                Stocks stocks;
                stocks= new Stocks(StocksData.get(0), StocksData.get(1), 0, 0, 0);
                Log.d(TAG, "onPostExecute: Stocks1: " +stocks);
                mainActivity.updateStockData(stocks);
            }
        }
    }

    protected Stocks parseJSON(String str1) {
        Log.d(TAG, "parseJSON: I am Here");
        try {
            JSONObject stockentry = new JSONObject(str1);
            String symbol = stockentry.getString("symbol");
            Log.d(TAG, "parseJSON: Stock Symbol" +symbol);
            String company_name = stockentry.getString("companyName");
            Log.d(TAG, "parseJSON: Stock Name" +company_name);
            String latest_price = stockentry.getString("latestPrice");
            Log.d(TAG, "parseJSON: Latest Price: " +latest_price);
            double latestPrice = 0.0;
            if (latest_price != null && !latest_price.trim().isEmpty() && !latest_price.trim().equals("null")) {
                latestPrice = Double.parseDouble(latest_price.trim());
                Log.d(TAG, "parseJSON: Latest Price: " +latestPrice);
            }
            String change = stockentry.getString("change");
            Log.d(TAG, "parseJSON: Change: "+change);
            double change1 = 0.0;
            if (change != null && !change.trim().isEmpty() && !change.trim().equals("null")) {
                change1 = Double.parseDouble(change.trim());
            }
            String change_percentage = stockentry.getString("changePercent");
            Log.d(TAG, "parseJSON: Change Percentage: " +change_percentage);
            double changePercent = 0.0;
            if (change_percentage != null && !change_percentage.trim().isEmpty() && !change_percentage.trim().equals("null")) {
                changePercent = Double.parseDouble(change_percentage.trim());
            }
            Stocks stock = new Stocks(symbol, company_name, latestPrice, change1, changePercent);
            Log.d(TAG, "parseJSON: Stock Symbol and Company Name: " + symbol + ", " + company_name);
            return stock;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
