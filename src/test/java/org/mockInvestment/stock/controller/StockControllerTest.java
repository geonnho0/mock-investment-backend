package org.mockInvestment.stock.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.domain.StockPrice;
import org.mockInvestment.stock.domain.StockPriceHistory;
import org.mockInvestment.stock.dto.StockCurrentPriceResponse;
import org.mockInvestment.stock.dto.StockPriceHistoriesResponse;
import org.mockInvestment.stock.dto.StockPriceHistoryResponse;
import org.mockInvestment.stock.util.PeriodExtractor;
import org.mockInvestment.util.ControllerTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockControllerTest extends ControllerTest {


    @Test
    @DisplayName("유효한 코드로 현재 주가를 요청한다.")
    void findStockCurrentPrice() {
        StockPrice stockPrice = new StockPrice(1.0, 1.0, 1.0, 1.0, 1.0);
        StockPriceHistory history = new StockPriceHistory(stockPrice, 1L);
        StockCurrentPriceResponse response = new StockCurrentPriceResponse(history.getPrice().getCurr());
        when(stockService.findStockCurrentPrice(any(String.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices?code=US1")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가를 불러오면, 404 에러를 반환한다.")
    void findStockCurrentPrice_exception_invalidCode() {
        when(stockService.findStockCurrentPrice(any(String.class)))
                .thenThrow(new StockNotFoundException());

        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices?code=XXX")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/fail/invalidCode"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    @DisplayName("최근 3개월 동안의 주가 정보를 반환한다.")
    void findStockPriceHistoriesForThreeMonths() {
        List<StockPriceHistoryResponse> responses = new ArrayList<>();
        int count = 4;
        for (int i = 0; i < count; i++) {
            responses.add(new StockPriceHistoryResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
        }
        StockPriceHistoriesResponse response = new StockPriceHistoriesResponse("US1", responses);
        when(stockService.findStockPriceHistories(any(String.class), any(PeriodExtractor.class)))
                .thenReturn(response);

        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices/US1/candles/3m")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/candles/success"))
                .statusCode(HttpStatus.OK.value());
    }

}