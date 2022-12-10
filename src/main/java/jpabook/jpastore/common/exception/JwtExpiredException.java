package jpabook.jpastore.common.exception;

public class JwtExpiredException extends RuntimeException{
    public JwtExpiredException() {
        super();
    }

    public JwtExpiredException(String message) {
        super(message);
    }

    public JwtExpiredException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtExpiredException(Throwable cause) {
        super(cause);
    }
}
