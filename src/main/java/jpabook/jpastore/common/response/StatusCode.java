package jpabook.jpastore.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    private final int statusCode;
    private final String codeMessage;
}
