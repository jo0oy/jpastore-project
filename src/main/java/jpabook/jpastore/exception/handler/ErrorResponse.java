package jpabook.jpastore.exception.handler;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ErrorResponse<T> {
    private int statusCode;
    private ZonedDateTime timestamp;
    private T error;

    public ErrorResponse(final int statusCode, final ZonedDateTime timestamp) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.error = null;
    }

    public static <T> ErrorResponse<T> error(final int statusCode, final ZonedDateTime time) {
        return error(statusCode, time, null);
    }

    public static <T> ErrorResponse<T> error(final int statusCode, final ZonedDateTime time, final T t) {
        return ErrorResponse.<T>builder()
                .error(t)
                .timestamp(time)
                .statusCode(statusCode)
                .build();
    }
 }
