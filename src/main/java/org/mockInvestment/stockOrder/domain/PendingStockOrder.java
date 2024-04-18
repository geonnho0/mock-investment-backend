package org.mockInvestment.stockOrder.domain;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.stockPrice.dto.RecentStockPrice;

public record PendingStockOrder(Long id, String code, Long memberId, boolean buy, Double bidPrice, Long quantity) {

    public static PendingStockOrder from(StockOrder stockOrder, Member member) {
        return new PendingStockOrder(stockOrder.getId(), stockOrder.getStockTicker(), member.getId(),
                stockOrder.getStockOrderType() == StockOrderType.BUY, stockOrder.getBidPrice(), stockOrder.getQuantity());
    }

    public boolean orderedBy(Long memberId) {
        return this.memberId == memberId;
    }

    public boolean cannotExecute(RecentStockPrice recentStockPrice) {
        return recentStockPrice.low() > bidPrice || bidPrice > recentStockPrice.high();
    }

    public StockOrderType stockOrderType() {
        return buy ? StockOrderType.BUY : StockOrderType.SELL;
    }

    public boolean isBuy() {
        return stockOrderType() == StockOrderType.BUY;
    }

    public Double totalBidPrice() {
        return quantity * bidPrice;
    }

}
