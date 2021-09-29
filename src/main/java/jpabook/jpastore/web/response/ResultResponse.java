package jpabook.jpastore.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ResultResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    public ResultResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = null;
    }

    public static<T> ResultResponse<T> res(final StatusCode status, final ResponseMessage message) {
        return res(status, message, null);
    }

    public static<T> ResultResponse<T> res(final StatusCode status, final ResponseMessage message, final T t) {
        return ResultResponse.<T>builder()
                .data(t)
                .statusCode(status.getStatusCode())
                .message(message.getMessage())
                .build();
    }
}
