package jpabook.jpastore.security.oauth2.info;

import jpabook.jpastore.domain.member.ProviderType;
import lombok.ToString;

import java.util.Map;

@ToString
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    protected ProviderType providerType;

    public OAuth2UserInfo(Map<String, Object> attributes,
                          ProviderType providerType) {
        this.attributes = attributes;
        this.providerType = providerType;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public ProviderType getProviderType() {
        return this.providerType;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

}
