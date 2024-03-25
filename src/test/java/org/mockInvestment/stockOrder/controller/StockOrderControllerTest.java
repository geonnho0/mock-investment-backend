package org.mockInvestment.stockOrder.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.advice.exception.InvalidStockOrderTypeException;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.stockOrder.dto.StockOrderCancelRequest;
import org.mockInvestment.stockOrder.dto.StockOrderHistoriesResponse;
import org.mockInvestment.stockOrder.dto.StockOrderHistoryResponse;
import org.mockInvestment.util.ControllerTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockOrderControllerTest extends ControllerTest {

    @Test
    @DisplayName("주식 구매 요청을 성공적으로 처리하면, 201을 반환한다.")
    void requestStockBuyOrder() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");

        doNothing()
                .when(stockOrderService)
                .createStockOrder(any(AuthInfo.class), anyString(), any(NewStockOrderRequest.class));

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
    @DisplayName("주식 판매 요청을 성공적으로 처리하면, 201을 반환한다.")
    void requestStockSellOrder() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "SELL");

        doNothing()
                .when(stockOrderService)
                .createStockOrder(any(AuthInfo.class), anyString(), any(NewStockOrderRequest.class));

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
    @DisplayName("주식 주문 요청이 유효하지 않으면, 400 에러를 반환한다.")
    void requestStockOrder_exception_invalidOrderType() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "XXX");

        doThrow(new InvalidStockOrderTypeException())
                .when(stockOrderService)
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
    @DisplayName("주식 주문 요청을 성공적으로 취소하면, 204를 반환한다.")
    void requestCancelStockOrder() {
        StockOrderCancelRequest request = new StockOrderCancelRequest(1L);

        doNothing()
                .when(stockOrderService)
                .cancelStockOrder(any(AuthInfo.class), any(StockOrderCancelRequest.class));

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .body(request)
                .when().delete("/stocks/orders")
                .then().log().all()
                .assertThat()
                .apply(document("stock-orders/success/cancel"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("특정 유저의 주식 주문 요청을 조회한다.")
    void findStockOrderHistories() {
        List<StockOrderHistoryResponse> historyResponses = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            historyResponses.add(new StockOrderHistoryResponse(i, LocalDate.now(), "BUY",
                    1.0 + i, 1 + i, "STOCK NAME"));
        StockOrderHistoriesResponse response = new StockOrderHistoriesResponse(historyResponses);

        when(stockOrderService.findStockOrderHistories(anyLong()))
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
    @DisplayName("내 주식 주문 요청을 조회한다.")
    void findMyStockOrderHistoriesByCode() {
        List<StockOrderHistoryResponse> historyResponses = new ArrayList<>();
        for (long i = 0; i < 5; i++)
            historyResponses.add(new StockOrderHistoryResponse(i, LocalDate.now(), "BUY",
                    1.0 + i, 1 + i, "STOCK NAME"));
        StockOrderHistoriesResponse response = new StockOrderHistoriesResponse(historyResponses);

        when(stockOrderService.findMyStockOrderHistoriesByCode(any(AuthInfo.class), anyString()))
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