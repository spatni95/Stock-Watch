package com.example.stockwatch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StocksHolder extends RecyclerView.ViewHolder {

    public TextView symbol_holder;
    public TextView name_holder;
    public TextView price_holder;
    public TextView change_holder;
    public TextView changePercentage_holder;

    public StocksHolder(@NonNull View itemView) {
        super(itemView);
        symbol_holder = itemView.findViewById(R.id.symbol);
        price_holder = itemView.findViewById(R.id.price);
        change_holder = itemView.findViewById(R.id.change);
        changePercentage_holder = itemView.findViewById(R.id.change_percent);
        name_holder = itemView.findViewById(R.id.name);

    }
}
