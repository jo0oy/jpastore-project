package jpabook.jpastore.domain.member;

import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Objects;

@ToString
@Getter
@Embeddable
public class OAuthInfo {
    private String oauthId;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Builder(builderClassName = "OAuthInfoBuilder", builderMethodName = "OAuthInfoBuilder")
    public OAuthInfo(String oauthId,
                     ProviderType providerType) {

        if(!StringUtils.hasText(oauthId)) throw new IllegalArgumentException("InvalidParam. oauthId");
        if(Objects.isNull(providerType)) throw new IllegalArgumentException("InvalidParam. providerType");

        this.oauthId = oauthId;
        this.providerType = providerType;
    }

    public OAuthInfo() {
        this.oauthId = "NONE";
        this.providerType = ProviderType.NONE;
    }
}
