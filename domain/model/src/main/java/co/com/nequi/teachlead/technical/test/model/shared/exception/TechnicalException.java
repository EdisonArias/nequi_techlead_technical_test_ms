package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.Getter;

@Getter
public class TechnicalException extends AppException {

    protected TechnicalException(TechnicalType type, String message) {
        super(type, message);
    }

    protected TechnicalException(TechnicalType type, String message, Throwable cause) {
        super(type, message, cause);
    }
}
