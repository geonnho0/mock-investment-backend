package org.mockInvestment.stock.repository;

import org.mockInvestment.stock.dto.LastStockInfo;

import java.util.Optional;

public interface LastStockInfoCacheRepository {

    Optional<LastStockInfo> findByStockCode(String code);

}
