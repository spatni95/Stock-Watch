package com.example.stockwatch;

public class Stocks implements Comparable<Stocks> {
    public String stock_symbol;
    public String stock_companyName;
    public Double stock_latestPrice;
    public Double stock_change;
    public Double stock_changePercent;

    public Stocks(String stock_symbol, String stock_companyName, Double stock_latestPrice, Double stock_change, Double stock_changePercent) {
        this.stock_symbol = stock_symbol;
        this.stock_companyName = stock_companyName;
        this.stock_latestPrice = stock_latestPrice;
        this.stock_change = stock_change;
        this.stock_changePercent = stock_changePercent;
    }

    public Stocks(String stock_symbol, String stock_companyName, int i, int i1, int i2) {
    }

    @Override
    public int compareTo(Stocks s) {
        return getStock_symbol().compareTo(s.getStock_symbol());
    }

    public String getStock_symbol() {
        return stock_symbol;
    }

    public void setStock_symbol(String stock_symbol) {
        this.stock_symbol = stock_symbol;
    }

    public String getStock_companyName() {
        return stock_companyName;
    }

    public void setStock_companyName(String stock_companyName) {
        this.stock_companyName = stock_companyName;
    }

    public Double getStock_latestPrice() {
        return stock_latestPrice;
    }

    public void setStock_latestPrice(Double stock_latestPrice) {
        this.stock_latestPrice = stock_latestPrice;
    }

    public Double getStock_change() {
        return stock_change;
    }

    public void setStock_change(Double stock_change) {
        this.stock_change = stock_change;
    }

    public Double getStock_changePercent() {
        return stock_changePercent;
    }

    public void setStock_changePercent(Double stock_changePercent) {
        this.stock_changePercent = stock_changePercent;
    }
}
