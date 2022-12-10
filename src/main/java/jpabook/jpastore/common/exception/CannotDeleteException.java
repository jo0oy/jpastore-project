package jpabook.jpastore.common.exception;

public class CannotDeleteException extends RuntimeException {
    public CannotDeleteException() {
        super();
    }

    public CannotDeleteException(String message) {
        super(message);
    }
}
