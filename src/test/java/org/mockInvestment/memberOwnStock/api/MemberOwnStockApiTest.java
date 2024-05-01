package org.mockInvestment.memberOwnStock.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStockValueResponse;
import org.mockInvestment.memberOwnStock.dto.MemberOwnStocksResponse;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class MemberOwnStockApiTest extends ApiTest {

    @Test
    @DisplayName("코드로 본인이 보유한 주식의 정보를 요청한다.")
    void findMyOwnStocks() {
        List<MemberOwnStockResponse> ownStocks = new ArrayList<>();
        ownStocks.add(new MemberOwnStockResponse(1L, 1.0, 5L, "CODE", "NAME"));
        MemberOwnStocksResponse response = new MemberOwnStocksResponse(ownStocks);
        when(memberOwnStockFindService.findMyOwnStocksFilteredByCode(any(AuthInfo.class), anyString()))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/own-stocks/me")
                .then().log().all()
                .assertThat()
                .apply(document("own-stocks/me/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("특정 날짜의 본인이 보유한 주식들의 총 가격을 요청한다.")
    void findMyOwnStockValue() {
        MemberOwnStockValueResponse response = new MemberOwnStockValueResponse(10.0, 5.0);
        when(memberOwnStockFindService.findMyOwnStockTotalValue(any(AuthInfo.class), any(LocalDate.class)))
                .thenReturn(response);

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .when().get("/own-stocks/me/total-value?date=2022-02-16")
                .then().log().all()
                .assertThat()
                .apply(document("own-stocks/me/total-value/success"))
                .statusCode(HttpStatus.OK.value());
    }

}