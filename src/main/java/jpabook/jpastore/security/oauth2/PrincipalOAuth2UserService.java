package jpabook.jpastore.security.oauth2;

import jpabook.jpastore.common.exception.OAuth2AuthenticationProcessingException;
import jpabook.jpastore.domain.auth.AuthMember;
import jpabook.jpastore.domain.member.Member;
import jpabook.jpastore.domain.member.MemberRepository;
import jpabook.jpastore.domain.member.OAuthInfo;
import jpabook.jpastore.domain.member.ProviderType;
import jpabook.jpastore.security.oauth2.info.OAuth2UserInfo;
import jpabook.jpastore.security.oauth2.info.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try{

            return this.process(userRequest, oAuth2User);

        } catch (Exception e) {

            log.error("OAuth2 loadUser Exception = {}", e.getMessage());
            throw e;

        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        var providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        var oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, oAuth2User.getAttributes());

        log.info("*****************************************");
        log.info("OAuth2UserInfo = {}", oAuth2UserInfo);
        log.info("*****************************************");

        // 이메일이 존재하진 않을 경우
        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        // oauthId 생성
        var oauthId = providerType + "_" + oAuth2UserInfo.getId();

        var findByOAuthId = memberRepository.findMemberByOAuthId(oauthId);
        var findByEmail = memberRepository.findByEmail(oAuth2UserInfo.getEmail());

        Member member = null;
        if (findByOAuthId.isPresent()) {
            member = findByOAuthId.get();
            if (member.getOAuthInfo().getProviderType() != providerType) {
                throw new OAuth2AuthenticationException(new OAuth2Error("provider miss match"),
                        "ProviderType 이 정상적으로 매칭되지 않습니다.");
            }

            member = updateExistingMember(member, oAuth2UserInfo);

        } else if (findByEmail.isPresent()) {

            // 기존 회원 : 소셜로그인 등록이 처음인 경우
            if (findByEmail.get().getOAuthInfo().getProviderType() == ProviderType.NONE) {
                member = findByEmail.get();
                member.addOAuthInfo(OAuthInfo.OAuthInfoBuilder()
                                .oauthId(oauthId)
                                .providerType(oAuth2UserInfo.getProviderType()).build());

                member = updateExistingMember(member, oAuth2UserInfo);
            } else {
                throw new OAuth2AuthenticationException(new OAuth2Error("already have sns login"),
                        "이미 다른 소셜로그인이 등록되어 있습니다. 소셜로그인 등록은 1개만 가능합니다.");
            }
        } else {
            member = registerNewMember(oAuth2UserInfo);
        }

        return new AuthMember(member, oAuth2User.getAttributes());
    }

    private Member updateExistingMember(Member member, OAuth2UserInfo oAuth2UserInfo) {
        log.info("*******이미 존재하는 회원 정보 업데이트 처리********");
        member.update(null, oAuth2UserInfo.getEmail());
        return memberRepository.saveAndFlush(member);
    }

    private Member registerNewMember(OAuth2UserInfo userInfo) {

        var providerType = userInfo.getProviderType();

        var username = UUID.randomUUID().toString().substring(0, 10);
        var oauthId = providerType + "_" + userInfo.getId();

        return memberRepository.saveAndFlush(
                Member.OAuthMemberBuilder()
                        .username(username)
                        .email(userInfo.getEmail())
                        .phoneNumber(null)
                        .oAuthInfo(
                                OAuthInfo.OAuthInfoBuilder()
                                        .oauthId(oauthId)
                                        .providerType(providerType)
                                        .build()
                        )
                        .build()
        );
    }
}
