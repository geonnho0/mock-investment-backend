package org.mockInvestment.balance.api;

import org.mockInvestment.util.ApiTest;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class BalanceApiTest extends ApiTest {

//    @Test
//    @DisplayName("본인의 계좌 금액을 조회한다.")
//    void requestStockBuyOrder() {
//        CurrentBalanceResponse response = new CurrentBalanceResponse(1.0);
//
//        when(balanceService.findBalance(any(AuthInfo.class)))
//                .thenReturn(response);
//
//        restDocs
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .cookies(cookies)
//                .when().get("/balance/me")
//                .then().log().all()
//                .assertThat()
//                .apply(document("balance/me/success"))
//                .statusCode(HttpStatus.OK.value());
//    }

}