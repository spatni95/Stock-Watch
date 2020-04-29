package com.example.stockwatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class StockDownload extends AsyncTask<String, Void, String> {
    private static final String TAG = "StockDownload";
    private final String getstockUrl = "https://api.iextrading.com/1.0/ref-data/symbols";
    private HashMap<String, String> stock_Data = new HashMap<>();
    private MainActivity mainActivity;

    public StockDownload(MainActivity ma){
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri dataUri = Uri.parse(getstockUrl);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "doInBackground: Loading Stock Data " + urlToUse);

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
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Error: ", e);
            return null;
        }
        Log.d(TAG, "doInBackground: Data is here: " + stringbuilder.toString());
        return stringbuilder.toString();
    }


@Override
    protected void onPostExecute(String str1) {
        HashMap<String, String> stock_Data = parseJSON(str1);
        mainActivity.updateStock(stock_Data);
    }

    protected HashMap<String, String> parseJSON(String str1) {
        try {
            JSONArray jsonObject = new JSONArray(str1);
            Log.d(TAG, "parseJSON: Json Length: " +jsonObject);
            for (int i = 0; i < jsonObject.length(); i++) {
                JSONObject jStock = (JSONObject) jsonObject.get(i);
                String symbol = jStock.getString("symbol");
                Log.d(TAG, "parseJSON: Symbol: " +symbol);
                String company_name = jStock.getString("name");
                Log.d(TAG, "parseJSON: Name: " +company_name);
                stock_Data.put(symbol, company_name);
            }
            return stock_Data;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
