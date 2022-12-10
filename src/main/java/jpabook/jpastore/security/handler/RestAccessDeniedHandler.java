package jpabook.jpastore.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jpabook.jpastore.common.exception.handler.ErrorCode;
import jpabook.jpastore.common.exception.handler.ErrorResponse;
import jpabook.jpastore.common.response.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.error("handling {}, message={}", accessDeniedException.getClass().toString(), accessDeniedException.getMessage());

        var errorResponse = ErrorResponse.error(
                StatusCode.FORBIDDEN.getStatusCode(),
                ErrorResponse.Error.builder()
                        .ex(accessDeniedException.getClass().getSimpleName())
                        .code(ErrorCode.AUTHORIZATION_ERROR.getErrorCode())
                        .message(accessDeniedException.getMessage())
                        .build()

        );

        ObjectMapper mapper = new ObjectMapper();

        // LocalDateTime 원하는 포맷으로 변환하기 위해
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var errorResponseJson = mapper.writeValueAsString(errorResponse);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");

        response.getWriter().write(errorResponseJson);
        response.flushBuffer();
    }
}
