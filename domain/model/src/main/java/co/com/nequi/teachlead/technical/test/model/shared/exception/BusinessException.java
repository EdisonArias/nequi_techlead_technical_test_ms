package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.Getter;

@Getter
public class BusinessException extends AppException {

    protected BusinessException(BusinessType type, String message) {
        super(type, message);
    }

    protected BusinessException(BusinessType type, String message, Throwable cause) {
        super(type, message, cause);
    }
}
