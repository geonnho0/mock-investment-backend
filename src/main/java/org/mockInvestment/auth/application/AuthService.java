package org.mockInvestment.auth.application;

import lombok.RequiredArgsConstructor;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;


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
        return memberRepository.findByUsername(oAuth2UserAttributes.getUsername())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .role("ROLE_USER")
                            .email(oAuth2UserAttributes.getEmail())
                            .username(oAuth2UserAttributes.getUsername())
                            .build();
                    return memberRepository.save(newMember);
                });
    }

}