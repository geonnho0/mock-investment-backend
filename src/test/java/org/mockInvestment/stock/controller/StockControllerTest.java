package org.mockInvestment.stock.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.StockNotFoundException;
import org.mockInvestment.stock.dto.StockInfoSummariesResponse;
import org.mockInvestment.stock.dto.StockInfoSummaryResponse;
import org.mockInvestment.stock.dto.StockPriceCandlesResponse;
import org.mockInvestment.stock.dto.StockPriceCandleResponse;
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
    @DisplayName("유효한 코드로 현재 주가(들)에 대한 간략한 정보를 요청한다.")
    void findStockSummaries() {
        List<StockInfoSummaryResponse> responses = new ArrayList<>();
        responses.add(new StockInfoSummaryResponse("US1", "MSFT", 2.0, 3.0));
        when(stockService.findStockInfoSummaries(any(List.class)))
                .thenReturn(new StockInfoSummariesResponse(responses));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stock-prices?code=US1")
                .then().log().all()
                .assertThat()
                .apply(document("stock-prices/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("유효하지 않은 코드로 현재 주가(들)에 대한 간략한 정보를 요청하면, 404 에러를 반환한다.")
    void findStockSummaries_exception_invalidCode() {
        when(stockService.findStockInfoSummaries(any(List.class)))
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
        List<StockPriceCandleResponse> responses = new ArrayList<>();
        int count = 4;
        for (int i = 0; i < count; i++) {
            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
        }
        StockPriceCandlesResponse response = new StockPriceCandlesResponse("US1", responses);
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