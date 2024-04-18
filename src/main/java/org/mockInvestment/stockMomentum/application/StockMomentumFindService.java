package org.mockInvestment.stockMomentum.application;

import org.mockInvestment.stockMomentum.dto.StockMomentumResponse;
import org.mockInvestment.stockMomentum.dto.StockMomentumsResponse;
import org.mockInvestment.stockValue.dto.StockValueResponse;
import org.mockInvestment.stockValue.dto.StockValuesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockMomentumFindService {

    public StockMomentumsResponse findStockMomentumsByCodesAtDate(List<String> stockCodes, String date) {
        RestTemplate restTemplate = new RestTemplate();
        List<StockMomentumResponse> momentums = new ArrayList<>();
        for (String stockCode : stockCodes) {
            String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8000/stock-momentums")
                    .queryParam("code", stockCode)
                    .queryParam("date", date)
                    .toUriString();
            StockMomentumResponse response = restTemplate.getForObject(url, StockMomentumResponse.class);
            momentums.add(response);
        }
        return new StockMomentumsResponse(momentums);
    }

    public StockMomentumsResponse findStockMomentumsRankingAtDate(String date) {
        RestTemplate restTemplate = new RestTemplate();
        String url = UriComponentsBuilder.fromHttpUrl("http://localhost:8000/stock-momentums/ranking")
                .queryParam("date", date)
                .toUriString();
        return restTemplate.getForObject(url, StockMomentumsResponse.class);
    }

}
