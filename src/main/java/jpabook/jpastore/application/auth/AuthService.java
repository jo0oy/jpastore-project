package jpabook.jpastore.application.auth;

import jpabook.jpastore.domain.auth.AuthCommand;
import jpabook.jpastore.domain.auth.AuthInfo;

public interface AuthService {
    AuthInfo.TokenInfo login(AuthCommand.LoginReq command);

    AuthInfo.AccessToken reissue(AuthCommand.ReissueReq command);

    void logout(AuthCommand.LogoutReq command);
}
