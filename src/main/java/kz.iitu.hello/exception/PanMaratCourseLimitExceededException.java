package kz.iitu.hello.exception;

public class PanMaratCourseLimitExceededException extends RuntimeException {
    public PanMaratCourseLimitExceededException(String message) {
        super(message);
    }
}
