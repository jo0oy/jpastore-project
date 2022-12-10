package jpabook.jpastore.common.exception.handler;

import jpabook.jpastore.common.response.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR("internal_server_error", StatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), "error-001"),
    AUTHENTICATION_ERROR("authentication_error", StatusCode.UNAUTHORIZED.getStatusCode(), "error-002"),
    AUTHORIZATION_ERROR("authorization_error", StatusCode.FORBIDDEN.getStatusCode(), "error-03"),
    ENTITY_NOT_FOUND("entity_not_found", StatusCode.BAD_REQUEST.getStatusCode(), "error-004"),
    ILLEGAL_ARGUMENT("illegal_argument", StatusCode.BAD_REQUEST.getStatusCode(), "error-005"),
    NOT_VALID_ARGUMENT("not_valid_arg", StatusCode.BAD_REQUEST.getStatusCode(), "error-006"),
    NOT_VALID_PARAMS("not_valid_params", StatusCode.BAD_REQUEST.getStatusCode(), "error-007"),
    CANNOT_DELETE("cannot_delete", StatusCode.BAD_REQUEST.getStatusCode(), "error-008"),
    DUPLICATE_PARAM("duplicate_param", StatusCode.BAD_REQUEST.getStatusCode(), "error-009"),
    NOT_ENOUGH_STOCK("not_enough_stock", StatusCode.BAD_REQUEST.getStatusCode(), "error-010-item");

    private final String errorName;
    private final int status;
    private String errorCode;

    ErrorCode(String errorName, int status) {
        this.errorName = errorName;
        this.status = status;
    }
}
