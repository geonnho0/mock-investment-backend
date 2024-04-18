package org.mockInvestment.stockPrice.api;

import org.mockInvestment.util.ApiTest;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockPriceApiTest extends ApiTest {

//    @Test
//    @DisplayName("특정 주식(들)의 현재 시세를 요청한다.")
//    void findStockPrices() {
//        List<StockPriceResponse> responses = new ArrayList<>();
//        for (int i = 0; i < 5; i++)
//            responses.add(new StockPriceResponse("CODE", "Stock Name", 2.0 + i, 3.0 + i));
//        when(stockPriceCandleFindService.findStockPrices(any(List.class)))
//                .thenReturn(new StockPricesResponse(responses));
//
//        restDocs
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices?code=CODE")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/success"))
//                .statusCode(HttpStatus.OK.value());
//    }
//
//    @Test
//    @DisplayName("유효하지 않은 코드로 현재 주가(들)에 대한 간략한 정보를 요청하면, 404를 반환한다.")
//    void findStockPrices_exception_invalidCode() {
//        when(stockPriceCandleFindService.findStockPrices(any(List.class)))
//                .thenThrow(new StockNotFoundException());
//
//        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices?code=INVALID-CODE")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/fail/invalidCode"))
//                .statusCode(HttpStatus.NOT_FOUND.value());
//    }
//
//    @Test
//    @DisplayName("최근 1주일 동안의 주가 정보를 반환한다.")
//    void findStockPriceCandlesForOneWeek() {
//        List<StockPriceCandleResponse> responses = new ArrayList<>();
//        for (int i = 0; i < 4; i++)
//            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
//        when(stockPriceCandleFindService.findStockPriceCandles(any(String.class), any(PeriodExtractor.class)))
//                .thenReturn(new StockPriceCandlesResponse("CODE", responses));
//
//        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices/CODE/candles/1w")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/candles/success/1w"))
//                .statusCode(HttpStatus.OK.value());
//    }
//
//    @Test
//    @DisplayName("최근 3개월 동안의 주가 정보를 반환한다.")
//    void findStockPriceCandlesForThreeMonths() {
//        List<StockPriceCandleResponse> responses = new ArrayList<>();
//        for (int i = 0; i < 4; i++)
//            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
//        when(stockPriceCandleFindService.findStockPriceCandles(any(String.class), any(PeriodExtractor.class)))
//                .thenReturn(new StockPriceCandlesResponse("CODE", responses));
//
//        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices/CODE/candles/3m")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/candles/success/3m"))
//                .statusCode(HttpStatus.OK.value());
//    }
//
//    @Test
//    @DisplayName("최근 1년 동안의 주가 정보를 반환한다.")
//    void findStockPriceCandlesForOneYear() {
//        List<StockPriceCandleResponse> responses = new ArrayList<>();
//        for (int i = 0; i < 4; i++)
//            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
//        when(stockPriceCandleFindService.findStockPriceCandles(any(String.class), any(PeriodExtractor.class)))
//                .thenReturn(new StockPriceCandlesResponse("CODE", responses));
//
//        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices/CODE/candles/1y")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/candles/success/1y"))
//                .statusCode(HttpStatus.OK.value());
//    }
//
//    @Test
//    @DisplayName("최근 5년 동안의 주가 정보를 반환한다.")
//    void findStockPriceCandlesForFiveYears() {
//        List<StockPriceCandleResponse> responses = new ArrayList<>();
//        for (int i = 0; i < 4; i++)
//            responses.add(new StockPriceCandleResponse(LocalDate.now(), 1.0, 1.0, 1.0, 1.0, 1L));
//        when(stockPriceCandleFindService.findStockPriceCandles(any(String.class), any(PeriodExtractor.class)))
//                .thenReturn(new StockPriceCandlesResponse("CODE", responses));
//
//        restDocs.contentType(MediaType.APPLICATION_JSON_VALUE)
//                .when().get("/stock-prices/CODE/candles/5y")
//                .then().log().all()
//                .assertThat()
//                .apply(document("stock-prices/candles/success/5y"))
//                .statusCode(HttpStatus.OK.value());
//    }

}