package org.mockInvestment.member.application;

import org.junit.jupiter.api.Test;
import org.mockInvestment.member.dto.NicknameUpdateRequest;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.util.MockTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class MemberNicknameUpdateServiceTest extends MockTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberNicknameUpdateService memberNicknameUpdateService;


    @Test
    void 닉네임_변경() {
        when(memberRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(testMember));

        NicknameUpdateRequest request = new NicknameUpdateRequest("update nickname");

        memberNicknameUpdateService.updateNickname(testAuthInfo, request);

        assertThat(testMember.getNickname()).isEqualTo(request.nickname());
    }

}