package jpabook.jpastore.common.exception.handler;

import jpabook.jpastore.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> exceptionHandle(Exception ex) {
        if (ex instanceof AccessDeniedException) throw new AccessDeniedException(ex.getMessage());

        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(JwtExpiredException.class)
    public ResponseEntity<?> exceptionHandle(JwtExpiredException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.AUTHORIZATION_ERROR;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> exceptionHandle(MethodArgumentNotValidException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.NOT_VALID_ARGUMENT;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> exceptionHandle(IllegalArgumentException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.ILLEGAL_ARGUMENT;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> exceptionHandle(BadRequestException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.NOT_VALID_PARAMS;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<?> exceptionHandle(CannotDeleteException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.CANNOT_DELETE;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> exceptionHandle(EntityNotFoundException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.ENTITY_NOT_FOUND;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<?> exceptionHandle(NotEnoughStockException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.NOT_ENOUGH_STOCK;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler({DuplicateNameException.class, DuplicateEmailException.class})
    public ResponseEntity<?> duplicateExceptionHandle(RuntimeException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.DUPLICATE_PARAM;

        return ResponseEntity.badRequest()
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));
    }

    @ExceptionHandler({OAuth2AuthenticationProcessingException.class, OAuth2ProviderMissMatchException.class})
    public ResponseEntity<?> oauthExceptionHandle(RuntimeException ex) {
        log.error("handling {}, message={}", ex.getClass().toString(), ex.getMessage());
        var errorCode = ErrorCode.AUTHENTICATION_ERROR;

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.error(errorCode.getStatus()
                        , ErrorResponse.Error.builder()
                                .ex(errorCode.getErrorName())
                                .code(errorCode.getErrorCode())
                                .message(ex.getMessage()).build()));

    }
}
