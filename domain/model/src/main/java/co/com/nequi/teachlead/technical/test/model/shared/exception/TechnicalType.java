package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static co.com.nequi.teachlead.technical.test.model.shared.exception.Formats.EXCEPTION_MESSAGE;


@Getter
@AllArgsConstructor
public enum TechnicalType implements ExceptionType {

    INTERNAL_SERVER_ERROR(500, "Internal Server Error", ""),
    SERVICE_UNAVAILABLE(503, "Service Unavailable", ""),
    GATEWAY_TIMEOUT(504, "Gateway timeout", ""),
    BAD_GATEWAY(502, "Bad Gateway", ""),
    MONGO_DB_ERROR(INTERNAL_SERVER_ERROR.code, INTERNAL_SERVER_ERROR.getMessage(),
            "Technical error with MongoDB");

    private final Integer code;
    private final String message;
    private String description;


    @Override
    public TechnicalException build() {
        return new TechnicalException(this, this.message);
    }

    @Override
    public TechnicalException build(String message) {
        return new TechnicalException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message, message));
    }

    @Override
    public TechnicalException build(String message, Throwable cause) {
        return new TechnicalException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message, message), cause);
    }

    @Override
    public TechnicalException build(Throwable cause) {
        return new TechnicalException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message, cause.getMessage()),
                cause);
    }

}