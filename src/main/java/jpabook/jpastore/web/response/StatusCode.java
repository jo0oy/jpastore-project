package jpabook.jpastore.web.response;

import lombok.Getter;

@Getter
public enum StatusCode {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR");

    int statusCode;
    String codeMessage;

    StatusCode(int statusCode, String codeMessage) {
        this.statusCode = statusCode;
        this.codeMessage = codeMessage;
    }
}
