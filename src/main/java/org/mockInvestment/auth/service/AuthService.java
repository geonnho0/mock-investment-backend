package org.mockInvestment.auth.service;

import org.mockInvestment.balance.repository.BalanceRepository;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.auth.dto.CustomOAuth2User;
import org.mockInvestment.auth.dto.GoogleUserAttributes;
import org.mockInvestment.auth.dto.OAuth2UserAttributes;
import org.mockInvestment.balance.domain.Balance;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    private final BalanceRepository balanceRepository;


    public AuthService(MemberRepository memberRepository, BalanceRepository balanceRepository) {
        this.memberRepository = memberRepository;
        this.balanceRepository = balanceRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserAttributes oAuth2UserAttributes = null;
        if (registrationId.equals("google")) {
            oAuth2UserAttributes = new GoogleUserAttributes(oAuth2User.getAttributes());
        }

        Member member = getOrCreateMember(oAuth2UserAttributes);
        AuthInfo authInfo = new AuthInfo(member);
        return new CustomOAuth2User(authInfo);
    }

    private Member getOrCreateMember(OAuth2UserAttributes oAuth2UserAttributes) {
        Optional<Member> member = memberRepository.findByUsername(oAuth2UserAttributes.getUsername());
        if (member.isEmpty()) {
            Member newMember = Member.builder()
                    .name(oAuth2UserAttributes.getName())
                    .email(oAuth2UserAttributes.getEmail())
                    .role("ROLE_USER")
                    .username(oAuth2UserAttributes.getUsername())
                    .build();
            createBalance(newMember);
            return memberRepository.save(newMember);
        }
        member.get().setEmail(oAuth2UserAttributes.getEmail());
        member.get().setName(oAuth2UserAttributes.getName());
        return member.get();
    }

    private void createBalance(Member member) {
        Balance balance = new Balance(member);
        balanceRepository.save(balance);
    }
}