package org.mockInvestment.member.api;

import org.junit.jupiter.api.Test;
import org.mockInvestment.member.dto.NicknameUpdateRequest;
import org.mockInvestment.util.ApiTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

class MemberApiTest extends ApiTest {

    @Test
    void 본인_닉네임을_변경한다() {
        NicknameUpdateRequest request = new NicknameUpdateRequest("update nickname");

        restDocs
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookies(cookies)
                .body(request)
                .when().post("/member/me/nickname")
                .then().log().all()
                .assertThat()
                .apply(document("member/me/nickname/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}