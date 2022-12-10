package jpabook.jpastore.security.oauth2.info;

import jpabook.jpastore.domain.member.ProviderType;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

    public KakaoOAuth2UserInfo(Map<String, Object> attributes, ProviderType providerType) {
        super(attributes, providerType);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }
        return (String) properties.get("nickname");
    }

    @Override
    public String getEmail() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("kakao_account");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("email");
    }
}
