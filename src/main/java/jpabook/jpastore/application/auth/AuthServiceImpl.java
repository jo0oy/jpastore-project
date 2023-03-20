package jpabook.jpastore.application.auth;

import jpabook.jpastore.common.exception.JwtExpiredException;
import jpabook.jpastore.domain.auth.AuthCommand;
import jpabook.jpastore.domain.auth.AuthInfo;
import jpabook.jpastore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService{

    private static final String REFRESH_TOKEN_KEY_PREFIX = "RT:"; // redis 에 저장되는 refreshToken key = RT:{username}
    private static final String LOGOUT_KEY_PREFIX = "LOGOUT:"; // redis 에 저장되는 로그아웃 key = LOGOUT:{AccessToken}
    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, Object> jwtRedisTemplate;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Override
    public AuthInfo.TokenInfo login(AuthCommand.LoginReq command) {
        log.info("로그인 및 인증 토큰 발행 로직 실행");

        var authenticationToken
                = new UsernamePasswordAuthenticationToken(command.getUsername(), command.getPassword());

        var authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        var tokenInfo = tokenProvider.generateToken(authentication);

        // redis 에 refreshToken 저장
        jwtRedisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + authentication.getName(),
                tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    @Override
    public AuthInfo.AccessToken reissue(AuthCommand.ReissueReq command) {
        log.info("인증 토큰 재발행 로직 실행");
        var authentication = tokenProvider.getAuthentication(command.getAccessToken());
        var redisKey = REFRESH_TOKEN_KEY_PREFIX + authentication.getName();

        var refreshToken = (String) jwtRedisTemplate.opsForValue().get(redisKey);

        if (Objects.isNull(refreshToken)) {
            throw new JwtExpiredException("리프레시 토큰이 만료되었습니다. 재로그인 해주세요.");
        }

        // 리프레시 토큰이 유효한 경우
        // 이미 있는 리프레시 토큰 삭제
        jwtRedisTemplate.delete(redisKey);

        var tokenInfo = tokenProvider.generateToken(authentication);

        jwtRedisTemplate.opsForValue().set(redisKey, tokenInfo.getRefreshToken(),
                tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return AuthInfo.AccessToken.builder()
                .accessToken(tokenInfo.getAccessToken())
                .build();
    }

    @Override
    public void logout(AuthCommand.LogoutReq command) {
        log.info("로그아웃 로직 실행");

        // AccessToken 검증
        var accessToken = command.getAccessToken();
        if (!tokenProvider.validateToken(accessToken)) {
            log.info("로그아웃 실패. 유효하지 않은 토큰입니다.");
            throw new JwtExpiredException("로그아웃 할 수 없습니다. 유효하지 않은 토큰 요청값으로 인증이 만료되었습니다.");
        }

        // refreshToken 제거
        var authentication = tokenProvider.getAuthentication(accessToken);
        var refreshTokenKey = REFRESH_TOKEN_KEY_PREFIX + authentication.getName();
        jwtRedisTemplate.delete(refreshTokenKey);

        // SecurityContext clear
        SecurityContextHolder.clearContext();

        // logout 여부 체크할 accessToken Redis 에 입력
        var logoutKey = LOGOUT_KEY_PREFIX + accessToken; // 'LOGOUT:{토큰값}' --> key
        jwtRedisTemplate.opsForValue().set(logoutKey, "logout",
                tokenProvider.getLeftExpirationFromNow(accessToken), TimeUnit.MILLISECONDS);
    }
}
