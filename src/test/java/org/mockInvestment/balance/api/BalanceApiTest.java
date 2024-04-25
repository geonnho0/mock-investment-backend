package org.mockInvestment.balance.api;

import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.dto.CurrentBalanceResponse;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class BalanceApiTest extends ApiTest {

    @Test
    void 본인의_계좌_금액을_조회한다() {
        CurrentBalanceResponse response = new CurrentBalanceResponse(1.0);

        when(balanceFindService.findBalance(any(AuthInfo.class)))
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