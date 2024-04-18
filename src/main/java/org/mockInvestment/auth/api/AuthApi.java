package org.mockInvestment.auth.api;

import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.global.auth.Login;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
public class AuthApi {

    @GetMapping("/loginInfo")
    public ResponseEntity<AuthInfo> oauthLoginInfo(@Login AuthInfo authInfo) {
        return ResponseEntity.ok(authInfo);
    }
}
