package org.mockInvestment.stockPrice.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockPrice.dto.StockPriceCandleResponse;
import org.mockInvestment.stockPrice.dto.StockPriceCandlesResponse;
import org.mockInvestment.stockPrice.dto.StockPriceResponse;
import org.mockInvestment.stockPrice.dto.StockPricesResponse;
import org.mockInvestment.stockPrice.util.PeriodExtractor;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockPriceApiTest extends ApiTest {

    @Test
    @DisplayName("특정 주식(들)의 특정 날짜의 시세를 요청한다.")
    void findStockPricesAtDate() {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            responses.add(new StockPriceResponse("CODE", "Stock Name", 2.0 + i, 3.0 + i));
        when(stockPriceFindService.findStockPricesByCodeAtDate(any(List.class), any(LocalDate.class)))
                .thenReturn(new StockPricesResponse(responses));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices?date=2022-02-16&code=CODE")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 코드로 주가(들)에 대한 특정 날짜의 시세를 요청하면, 404를 반환한다.")
    void findStockPricesAtDate_exception_invalidCode() {
        when(stockPriceFindService.findStockPricesByCodeAtDate(any(List.class), any(LocalDate.class)))
                .thenThrow(new StockTickerNotFoundException());

        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices?date=2022-02-16&code=INVALID-CODE")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/fail/invalidCode"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("특정 날짜를 기준으로 최근 1년 동안의 주가 정보를 요청한다.")
    void findStockPriceCandlesForOneYear() {
        List<StockPriceCandleResponse> responses = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
        when(stockPriceCandleFindService.findStockPriceCandles(any(String.class), any(PeriodExtractor.class)))
                .thenReturn(new StockPriceCandlesResponse("CODE", responses));

        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices/CODE?period=1y&end=2024-04-25")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/candles/success/1y"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("즐겨찾기한 주식들의 특정 날짜의 시세를 요청한다.")
    void findAllLikedStockPricesAtDate() {
        List<StockPriceResponse> responses = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            responses.add(new StockPriceResponse("CODE", "Stock Name", 2.0 + i, 3.0 + i));
        when(stockPriceFindService.findAllLikedStockPricesAtDate(any(LocalDate.class), any(AuthInfo.class)))
                .thenReturn(new StockPricesResponse(responses));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices/like?date=2022-02-16")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/success/like"))
                .statusCode(HttpStatus.OK.value());
    }

}