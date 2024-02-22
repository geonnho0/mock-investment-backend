package org.mockInvestment.auth.service;

import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.auth.dto.CustomOAuth2User;
import org.mockInvestment.auth.dto.GoogleUserAttributes;
import org.mockInvestment.auth.dto.OAuth2UserAttributes;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;


    public AuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
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
        AuthInfo authInfo = AuthInfo.builder()
                .id(member.getId())
                .name(member.getName())
                .username(member.getUsername())
                .role(member.getRole())
                .build();

        return new CustomOAuth2User(authInfo);
    }

    private Member getOrCreateMember(OAuth2UserAttributes oAuth2UserAttributes) {
        Optional<Member> member = memberRepository.findByUsername(oAuth2UserAttributes.getUsername());
        if (member.isEmpty()) {
            return memberRepository.save(Member.builder()
                .name(oAuth2UserAttributes.getName())
                .email(oAuth2UserAttributes.getEmail())
                .role("ROLE_USER")
                .username(oAuth2UserAttributes.getUsername())
                .build());
        }
        member.get().setEmail(oAuth2UserAttributes.getEmail());
        member.get().setName(oAuth2UserAttributes.getName());
        return member.get();
    }
}