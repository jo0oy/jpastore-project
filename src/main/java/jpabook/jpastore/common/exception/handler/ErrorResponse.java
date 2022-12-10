package jpabook.jpastore.common.exception.handler;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ErrorResponse<T> {
    private int statusCode;
    private LocalDateTime timestamp;
    private T error;

    public ErrorResponse(final int statusCode, final LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.error = null;
    }

    public static <T> ErrorResponse<T> error(final int statusCode) {
        return error(statusCode, null);
    }

    public static <T> ErrorResponse<T> error(final int statusCode, final T t) {
        return ErrorResponse.<T>builder()
                .error(t)
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .build();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Error {
        private String ex;
        private String code;
        private String message;

        public Error(String ex, String message) {
            this.ex = ex;
            this.message = message;
        }
    }
 }
