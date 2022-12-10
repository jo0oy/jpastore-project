package jpabook.jpastore.security.oauth2.info;

import jpabook.jpastore.domain.member.ProviderType;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case NAVER: return new NaverOAuth2UserInfo(attributes, ProviderType.NAVER);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes, ProviderType.KAKAO);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
