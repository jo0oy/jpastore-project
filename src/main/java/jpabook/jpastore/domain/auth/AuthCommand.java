package jpabook.jpastore.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class AuthCommand {

    @Getter
    @Builder
    @ToString
    public static class LoginReq {
        private String username;
        private String password;
    }

    @ToString
    @Getter
    @Builder
    public static class ReissueReq {
        private String accessToken;
    }

    @ToString
    @Getter
    @Builder
    public static class LogoutReq {
        private String accessToken;
    }
}
