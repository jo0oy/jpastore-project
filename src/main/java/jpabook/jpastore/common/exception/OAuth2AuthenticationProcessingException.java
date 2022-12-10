package jpabook.jpastore.common.exception;

public class OAuth2AuthenticationProcessingException extends RuntimeException {

    public OAuth2AuthenticationProcessingException() {
        super();
    }

    public OAuth2AuthenticationProcessingException(String message) {
        super(message);
    }

    public OAuth2AuthenticationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
