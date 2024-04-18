package org.mockInvestment.member.api;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.mockInvestment.member.application.MemberNicknameUpdateService;
import org.mockInvestment.member.dto.NicknameUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberApi {

    private final MemberNicknameUpdateService memberNicknameUpdateService;


    @PostMapping("/member/me/nickname")
    public ResponseEntity<Void> updateNickname(@Login AuthInfo authInfo,
                                               @RequestBody NicknameUpdateRequest request) {
        memberNicknameUpdateService.updateNickname(authInfo, request);
        return ResponseEntity.noContent().build();
    }

}
