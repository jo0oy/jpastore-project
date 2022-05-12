package jpabook.jpastore.exception.handler;

import jpabook.jpastore.web.response.StatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor
@Getter
public enum ErrorCode {

    ORDER_NOT_FOUND("order_not_found", StatusCode.BAD_REQUEST.getStatusCode(), "order-001"),
    MEMBER_NOT_FOUND("member_not_found", StatusCode.BAD_REQUEST.getStatusCode(), "member-001"),
    DUPLICATE_USERNAME("duplicate_username", StatusCode.BAD_REQUEST.getStatusCode(), "member-002"),
    ITEM_NOT_FOUND("item_not_found", StatusCode.BAD_REQUEST.getStatusCode(), "item-001"),
    NOT_ENOUGH_STOCK("not_enough_stock", StatusCode.BAD_REQUEST.getStatusCode(), "item-002"),
    DUPLICATE_NAME("duplicate_name", StatusCode.BAD_REQUEST.getStatusCode(), "category-001"),
    INTERNAL_SERVER_ERROR("internal_server_error",
            StatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), "general-001"),
    ILLEGAL_ARGUMENT("illegal_argument", StatusCode.BAD_REQUEST.getStatusCode(), "general-002"),
    NOT_VALID_ARGUMENT("not_valid_arg", StatusCode.BAD_REQUEST.getStatusCode(), "general-003"),
    CANNOT_DELETE("cannot_delete", StatusCode.BAD_REQUEST.getStatusCode(), "general-004");

    private final String errorName;
    private final int status;
    private String errorCode;

    ErrorCode(String errorName, int status) {
        this.errorName = errorName;
        this.status = status;
    }
}
