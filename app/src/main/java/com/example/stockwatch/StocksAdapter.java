package com.example.stockwatch;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StocksAdapter extends RecyclerView.Adapter<StocksHolder> {
    private static final String TAG = "StocksAdapter";
    private MainActivity mainActivity;
    private List<Stocks> Selected_Stocks;


    public StocksAdapter(List<Stocks> Selected_Stocks, MainActivity main) {
        this.Selected_Stocks = Selected_Stocks;
        mainActivity = main;
    }

    @NonNull
    @Override
    public StocksHolder onCreateViewHolder(@NonNull ViewGroup stockHolder, int i) {

        View itemView = LayoutInflater.from(stockHolder.getContext())
                .inflate(R.layout.stock_list_view, stockHolder, false);
        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StocksHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StocksHolder stocksHolder, int i) {
        Log.d(TAG, "onBindViewHolder: I am in BindView");
        Stocks stockItem = Selected_Stocks.get(i);
        stocksHolder.symbol_holder.setText(stockItem.getStock_symbol());
        stocksHolder.price_holder.setText(Double.toString(stockItem.getStock_latestPrice()));
        stocksHolder.change_holder.setText(String.format("%.2f", stockItem.getStock_change()));
        stocksHolder.changePercentage_holder.setText("("+String.format("%.2f", stockItem.getStock_changePercent()) + "%)");
        stocksHolder.name_holder.setText(stockItem.getStock_companyName());
        if (stockItem.getStock_change() > 0){
            stocksHolder.change_holder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_drop_up_black_24dp,0 ,0 ,0);
            stocksHolder.price_holder.setTextColor(Color.GREEN);
            stocksHolder.change_holder.setTextColor(Color.GREEN);
            stocksHolder.symbol_holder.setTextColor(Color.GREEN);
            stocksHolder.name_holder.setTextColor(Color.GREEN);
            stocksHolder.changePercentage_holder.setTextColor(Color.GREEN);
        }
        else{
            stocksHolder.change_holder.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_arrow_drop_down_black_24dp,0,0,0);
            stocksHolder.price_holder.setTextColor(Color.RED);
            stocksHolder.change_holder.setTextColor(Color.RED);
            stocksHolder.symbol_holder.setTextColor(Color.RED);
            stocksHolder.name_holder.setTextColor(Color.RED);
            stocksHolder.changePercentage_holder.setTextColor(Color.RED);
        }

    }

    @Override
    public int getItemCount() {
        return Selected_Stocks.size();
    }
}
