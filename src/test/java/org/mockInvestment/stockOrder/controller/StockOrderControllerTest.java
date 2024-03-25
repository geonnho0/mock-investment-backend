package org.mockInvestment.stockOrder.controller;

import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.stockOrder.dto.NewStockOrderRequest;
import org.mockInvestment.util.ControllerTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class StockOrderControllerTest extends ControllerTest {

    @Test
    void requestStockBuyOrder() {
        NewStockOrderRequest request = new NewStockOrderRequest(1.0, 1L, "BUY");

        doNothing().when(stockOrderService).createStockOrder(any(AuthInfo.class), anyString(), any(NewStockOrderRequest.class));

        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Access Token");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(map)
                .body(request)
                .when().post("/stocks/CODE/purchase")
                .then().log().all()
                .assertThat()
                .apply(document("stocks-order/purchase/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

}