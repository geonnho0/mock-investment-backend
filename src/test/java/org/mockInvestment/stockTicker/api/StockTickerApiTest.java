package org.mockInvestment.stockTicker.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockTicker.dto.StockTickerLikeResponse;
import org.mockInvestment.stockTicker.dto.StockTickerResponse;
import org.mockInvestment.stockTicker.dto.StockTickersResponse;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockTickerApiTest extends ApiTest {

    @Test
    @DisplayName("코드로 주식의 정보를 요청한다.")
    void findStockTickerByCode() {
        StockTickerResponse response = new StockTickerResponse("Stock name", "Stock code", false);
        when(stockTickerFindService.findStockTickerByCode(anyString(), any(AuthInfo.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/stock-tickers/CODE")
                .then().log().all()
                .assertThat()
                .apply(document("stock-tickers/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 주식의 좋아요를 변경한다.")
    void toggleStockTickerLike() {
        StockTickerLikeResponse response = new StockTickerLikeResponse(true);
        when(stockTickerLikeToggleService.toggleLike(anyString(), any(AuthInfo.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().put("/stock-tickers/CODE/like")
                .then().log().all()
                .assertThat()
                .apply(document("stock-tickers/like/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("키워드로 주식의 정보를 요청한다.")
    void findStockTickersByKeyword() {
        List<StockTickerResponse> responses = new ArrayList<>();
        responses.add(new StockTickerResponse("NAME", "NAM", false));
        responses.add(new StockTickerResponse("NAMED", "NA", true));
        StockTickersResponse response = new StockTickersResponse(responses);

        when(stockTickerFindService.findStockTickersByKeyword(anyString(), any(AuthInfo.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/stock-tickers/search?keyword=NA")
                .then().log().all()
                .assertThat()
                .apply(document("stock-tickers/search/success"))
                .statusCode(HttpStatus.OK.value());
    }

}