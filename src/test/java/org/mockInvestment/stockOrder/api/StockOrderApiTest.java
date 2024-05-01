package org.mockInvestment.stockOrder.api;

import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.stockOrder.dto.StockOrderHistoryResponse;
import org.mockInvestment.stockOrder.exception.InvalidStockOrderTypeException;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockOrderApiTest extends ApiTest {

    @Test
    void 주식_구매_요청_처리시_201_반환() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY", LocalDate.now());

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .body(request)
                .when().post("/stocks/CODE/order")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/success/buy"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 주식_판매_요청_처리시_201_반환() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "SELL", LocalDate.now());

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .body(request)
                .when().post("/stocks/CODE/order")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/success/sell"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 주식_주문_요청_유효하지_않으면_400_반환() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "XXX", LocalDate.now());

        doThrow(new InvalidStockOrderTypeException())
                .when(stockOrderCreateService)
                .createStockOrder(any(AuthInfo.class), anyString(), any(NewStockOrderRequest.class));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .body(request)
                .when().post("/stocks/CODE/order")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/fail/invalidOrderType"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 주식_주문_취소_처리시_204_반환() {
        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().delete("/stock-orders/1")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/success/cancel"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 특정_유저의_주식_주문_요청_조회() {
        List<StockOrderHistoryResponse> historyResponses = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            historyResponses.add(new StockOrderHistoryResponse(i, LocalDate.now(), "BUY",
                    1.0 + i, 1 + i, "STOCK NAME", "STOCK_CODE",
                    true, LocalDate.now()));
        StockOrderHistoriesResponse response = new StockOrderHistoriesResponse(historyResponses);

        when(stockOrderFindService.findAllStockOrderHistories(anyLong()))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/stocks/orders?member=1")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/histories/success/member"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    void 본인_주식_주문_요청_조회() {
        List<StockOrderHistoryResponse> historyResponses = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            historyResponses.add(new StockOrderHistoryResponse(i, LocalDate.now(), "BUY",
                    1.0 + i, 1 + i, "STOCK NAME", "STOCK_CODE",
                    true, LocalDate.now()));
        StockOrderHistoriesResponse response = new StockOrderHistoriesResponse(historyResponses);

        when(stockOrderFindService.findMyStockOrderHistoriesByCode(any(AuthInfo.class), anyString()))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/stocks/orders/me?code=CODE")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/histories/success/meWithCode"))
                .statusCode(HttpStatus.OK.value());
    }

}