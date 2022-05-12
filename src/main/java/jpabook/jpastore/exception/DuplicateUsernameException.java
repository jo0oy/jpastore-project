package jpabook.jpastore.exception;

public class DuplicateUsernameException extends RuntimeException{
    public DuplicateUsernameException() {
        super();
    }

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
