package jpabook.jpastore.web.dto.auth;

import lombok.*;

import javax.validation.constraints.NotBlank;

public class AuthDto {

    @ToString
    @Getter
    @Builder
    public static class LoginReq {
        @NotBlank(message = "{NotBlank.auth.username}")
        private String username;

        @NotBlank(message = "{NotBlank.auth.password}")
        private String password;
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReissueReq {
        private String accessToken;
    }

    @ToString
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LogoutReq {
        private String accessToken;
    }

    @ToString
    @Getter
    @Builder
    public static class TokenInfoResponse {
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
    }

    @ToString
    @Getter
    @Builder
    public static class AccessTokenResponse {
        private String accessToken;
    }
}
