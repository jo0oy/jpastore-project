package jpabook.jpastore.security.oauth2.info;

import jpabook.jpastore.domain.member.ProviderType;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo{

    private Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes, ProviderType providerType) {
        super(attributes, providerType);
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getId() {
        if (response == null) {
            return null;
        }

        return (String) response.get("id");
    }

    @Override
    public String getName() {
        if (response == null) {
            return null;
        }

        return (String) response.get("name");
    }

    @Override
    public String getEmail() {
        if (response == null) {
            return null;
        }

        return (String) response.get("email");
    }
}
