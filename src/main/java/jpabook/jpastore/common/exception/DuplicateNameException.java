package jpabook.jpastore.common.exception;

public class DuplicateNameException extends RuntimeException{
    public DuplicateNameException() {
        super("이미 존재하는 회원입니다.");
    }

    public DuplicateNameException(String message) {
        super(message);
    }

    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
