package jpabook.jpastore.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jpabook.jpastore.common.exception.handler.ErrorCode;
import jpabook.jpastore.common.exception.handler.ErrorResponse;
import jpabook.jpastore.common.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        log.error("handling {}, message={}", authException.getClass().toString(), authException.getMessage());

        var errorResponse = ErrorResponse.error(
                StatusCode.UNAUTHORIZED.getStatusCode(),
                ErrorResponse.Error.builder()
                        .ex(authException.getClass().getName())
                        .code(ErrorCode.AUTHENTICATION_ERROR.getErrorCode())
                        .message(authException.getMessage())
                        .build()

        );

        ObjectMapper mapper = new ObjectMapper();

        // LocalDateTime 원하는 포맷으로 변환하기 위해
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var errorResponseJson = mapper.writeValueAsString(errorResponse);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");

        response.getWriter().write(errorResponseJson);
        response.flushBuffer();
    }
}
