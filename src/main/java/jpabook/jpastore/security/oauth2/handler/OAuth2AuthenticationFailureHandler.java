package jpabook.jpastore.security.oauth2.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jpabook.jpastore.common.exception.handler.ErrorCode;
import jpabook.jpastore.common.exception.handler.ErrorResponse;
import jpabook.jpastore.common.response.ResultResponse;
import jpabook.jpastore.common.response.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.info("OAuth2AuthenticationFailureHandler.onAuthenticationFailure");

//        String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue)
//                .orElse(("/"));

        log.info("exception={}", exception.getMessage());

//        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("error", exception.getLocalizedMessage())
//                .build().toUriString();
//
//        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        var res = ErrorResponse.error(StatusCode.UNAUTHORIZED.getStatusCode(),
                ErrorResponse.Error.builder()
                        .ex(exception.getClass().getName())
                        .code(ErrorCode.AUTHENTICATION_ERROR.getErrorCode())
                        .message(exception.getMessage())
                .build());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        var jsonErrorRes = mapper.writeValueAsString(res);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(jsonErrorRes);
        response.flushBuffer();
    }
}

