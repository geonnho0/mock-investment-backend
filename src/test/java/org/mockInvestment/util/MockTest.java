package org.mockInvestment.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.balance.domain.Balance;
import org.mockInvestment.member.domain.Member;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class MockTest {

    protected Member testMember;

    protected Balance testBalance;

    protected AuthInfo testAuthInfo;


    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .role("USER")
                .username("USERNAME")
                .email("EMAIL")
                .build();
        testBalance = new Balance(testMember);
        testAuthInfo = new AuthInfo(testMember);
    }

}
