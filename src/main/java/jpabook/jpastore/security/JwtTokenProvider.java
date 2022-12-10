package jpabook.jpastore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jpabook.jpastore.common.exception.JwtExpiredException;
import jpabook.jpastore.domain.auth.AuthInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider implements InitializingBean {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static final String AUTHORITIES_KEY = "auth";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 8; // 8분

    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 15; // 15분

    private Key key;

    private final PrincipalUserDetailsService principalUserDetailsService;

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // jwt 토큰 생성
    public AuthInfo.TokenInfo generateToken(Authentication authentication) {

        var authorities = getAuthorities(authentication);
        long now = (new Date()).getTime();
        Date validity = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        Date refreshTokenValidity = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);

        var accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        var refreshToken = Jwts.builder()
                        .setExpiration(refreshTokenValidity)
                        .signWith(key, SignatureAlgorithm.HS512).compact();

        return AuthInfo.TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    // 인증 객체 반환
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        UserDetails principal = principalUserDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }


    // jwt 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
                log.info("만료된 JWT 토큰입니다.");
            throw new JwtExpiredException("만료된 토큰");
        } catch (UnsupportedJwtException e) {
                log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
                log.info("JWT 토큰이 잘못되었습니다.");
        }
            return false;
    }

    // 토큰 내용 파싱
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            return ex.getClaims();
        }
    }

    // 권한 리스트 추출
    private Collection<String> getAuthorities(Authentication authentication) {
        return Collections.singletonList(authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).findFirst().orElse(null));
    }

    // 전달받은 인증 토큰의 현재로부터 남은 만료 시간
    public long getLeftExpirationFromNow(String token) {
        var expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getExpiration();

        var now = new Date().getTime();

        return expiration.getTime() - now;
    }
}
