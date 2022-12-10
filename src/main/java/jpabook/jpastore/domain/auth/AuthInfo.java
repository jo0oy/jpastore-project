package jpabook.jpastore.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AuthInfo {
    @Getter
    @Builder
    @ToString
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }

    @Getter
    @Builder
    @ToString
    public static class AccessToken {
        private String accessToken;
    }
}
