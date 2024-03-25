package org.mockInvestment.balance.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.util.ControllerTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class BalanceControllerTest extends ControllerTest {

    @Test
    @DisplayName("본인의 계좌 금액을 조회한다.")
    void requestStockBuyOrder() {
        CurrentBalanceResponse response = new CurrentBalanceResponse(1.0);

        when(balanceService.findBalance(any(AuthInfo.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/balance/me")
                .then().log().all()
                .assertThat()
                .apply(document("balance/me/success"))
                .statusCode(HttpStatus.OK.value());
    }

}