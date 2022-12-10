package jpabook.jpastore.security.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jpabook.jpastore.common.response.ResponseMessage;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import jpabook.jpastore.domain.auth.AuthInfo;
import jpabook.jpastore.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
//    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

//    @Value("${app.oauth2.authorizedRedirectUris}")
//    private String authorizedRedirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("OAuth2AuthenticationSuccessHandler.onAuthenticationSuccess");

//        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
//            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            log.debug("Response has already been committed.");
            return;
        }

        // JWT Token 생성
        AuthInfo.TokenInfo tokenInfo = tokenProvider.generateToken(authentication);

        log.info("로그인 성공! 토큰 생성 = {}", tokenInfo);

        // redis 에 refreshToken 저장
        redisTemplate.opsForValue().set(authentication.getName(),
                tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        var accessToken = AuthInfo.AccessToken.builder()
                .accessToken(tokenInfo.getAccessToken()).build();

        var res = ResultResponse.res(StatusCode.OK, ResponseMessage.LOGIN_SUCCESS, accessToken);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var jsonData = mapper.writeValueAsString(res);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(jsonData);
        response.flushBuffer();
//

//        clearAuthenticationAttributes(request, response);
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
//        }
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
//        return UriComponentsBuilder.fromUriString(targetUrl)
//                .build().toUriString();
//    }

//    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//        super.clearAuthenticationAttributes(request);
//        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//
//    }

//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//        URI authorizedURI = URI.create(authorizedRedirectUri);
//        return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                && authorizedURI.getPort() == clientRedirectUri.getPort();
//    }
}
