package jpabook.jpastore.exception.handler;

import jpabook.jpastore.exception.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandle(RuntimeException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> exceptionHandle(MethodArgumentNotValidException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.NOT_VALID_ARGUMENT;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> exceptionHandle(IllegalArgumentException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        ,Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<?> exceptionHandle(CannotDeleteException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.CANNOT_DELETE;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> exceptionHandle(OrderNotFoundException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<?> exceptionHandle(MemberNotFoundException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.MEMBER_NOT_FOUND;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<?> exceptionHandle(DuplicateUsernameException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.DUPLICATE_USERNAME;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<?> exceptionHandle(ItemNotFoundException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.ITEM_NOT_FOUND;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<?> exceptionHandle(NotEnoughStockException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.NOT_ENOUGH_STOCK;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<?> exceptionHandle(DuplicateNameException ex) {
        log.info("handling {}", ex.getClass().toString());
        log.error(ex.getMessage());
        ErrorCode errorCode = ErrorCode.DUPLICATE_NAME;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ZonedDateTime.now(ZoneId.of("Z"))
                        , Error.builder()
                                .name(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }



    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    static class Error {
        private String name;
        private String code;
        private String message;

        public Error(String name, String message) {
            this.name = name;
            this.message = message;
        }
    }
}
