package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {

    private final transient ExceptionType type;

    protected AppException(ExceptionType type, String message) {
        super(message);
        this.type = type;
    }

    protected AppException(ExceptionType type, String message, Throwable cause) {
        super(message, cause);
        this.type = type;
    }
}

