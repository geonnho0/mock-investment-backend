package org.mockInvestment.stock.repository;

import org.mockInvestment.stock.domain.RecentStockInfo;

import java.util.Optional;

public interface RecentStockInfoCacheRepository {

    Optional<RecentStockInfo> findByStockCode(String code);

    void saveByCode(String code, RecentStockInfo entity);

}
