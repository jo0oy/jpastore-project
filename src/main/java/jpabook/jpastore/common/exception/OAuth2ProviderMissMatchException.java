package jpabook.jpastore.common.exception;

public class OAuth2ProviderMissMatchException extends RuntimeException {

    public OAuth2ProviderMissMatchException() {
        super();
    }

    public OAuth2ProviderMissMatchException(String message) {
        super(message);
    }

    public OAuth2ProviderMissMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
