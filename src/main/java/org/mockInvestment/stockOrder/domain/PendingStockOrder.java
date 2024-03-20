package org.mockInvestment.stockOrder.domain;


public record PendingStockOrder(long orderId, long stockId, double bidPrice) {
    public static PendingStockOrder of(StockOrder stockOrder) {
        return new PendingStockOrder(stockOrder.getId(), stockOrder.getStock().getId(), stockOrder.getBidPrice());
    }

    public boolean canConclude(double currentPrice) {
        return (Math.abs(bidPrice - currentPrice) / currentPrice) <= 0.06;
    }

}
