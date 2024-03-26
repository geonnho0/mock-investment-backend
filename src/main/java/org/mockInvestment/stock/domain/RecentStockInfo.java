package org.mockInvestment.stock.domain;


import org.mockInvestment.stock.domain.Stock;
import org.mockInvestment.stock.domain.StockPriceCandle;

public record RecentStockInfo(String symbol, String name, double base, double close, double curr, double high, double low, double open, long volume) {

    public RecentStockInfo(double base, Stock stock, StockPriceCandle recentPriceCandle) {
        this(stock.getSymbol(), stock.getName(), base,
                recentPriceCandle.getClose(),
                recentPriceCandle.getCurr(),
                recentPriceCandle.getHigh(),
                recentPriceCandle.getLow(),
                recentPriceCandle.getOpen(),
                recentPriceCandle.getVolume());
    }

}