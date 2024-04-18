package org.mockInvestment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MockTest {

    protected Member testMember;

    protected AuthInfo testAuthInfo;


    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .role("USER")
                .name("NAME")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        testAuthInfo = new AuthInfo(testMember);
    }

}
