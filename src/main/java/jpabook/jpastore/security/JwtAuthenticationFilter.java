package jpabook.jpastore.security;

import jpabook.jpastore.common.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtAuthenticationFilter 진입. 토큰 유무 체크");

        String requestURI = request.getRequestURI();

        log.info("requestURI = {}", requestURI);

        String token = HeaderUtils.getAccessToken(request);

        try {
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                // 로그아웃 처리된 토큰인지 확인
                var isLogout = (String) redisTemplate.opsForValue().get("LOGOUT:" + token);
                log.info("isLogout={}", !ObjectUtils.isEmpty(isLogout));
                if (ObjectUtils.isEmpty(isLogout)) {
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.info("Security Context 에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                }
            }

        } catch (Exception ex) {
            log.error("Security Context 에 인증 객체를 저장할 수 없습니다. exception={}", ex.getClass().getName());
        }

        log.info("before doFilter.. uri ={}", requestURI);
        filterChain.doFilter(request, response);
    }
}
