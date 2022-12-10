package jpabook.jpastore.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ResultResponse<T> {

    private LocalDateTime responseTime;
    private int statusCode;
    private String message;
    private T data;

    public ResultResponse(final LocalDateTime responseTime,
                          final int statusCode,
                          final String message) {
        this.responseTime = responseTime;
        this.statusCode = statusCode;
        this.message = message;
        this.data = null;
    }

    public static<T> ResultResponse<T> res(final StatusCode status,
                                           final ResponseMessage message) {
        return res(status, message, null);
    }

    public static<T> ResultResponse<T> res(final StatusCode status, final ResponseMessage message, final T t) {
        return ResultResponse.<T>builder()
                .responseTime(LocalDateTime.now())
                .data(t)
                .statusCode(status.getStatusCode())
                .message(message.getMessage())
                .build();
    }
}
