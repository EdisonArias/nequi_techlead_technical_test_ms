package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static co.com.nequi.teachlead.technical.test.model.shared.exception.Formats.EXCEPTION_MESSAGE;

@Getter
@AllArgsConstructor
public enum BusinessType implements ExceptionType {

    BAD_REQUEST(400, "Bad Request"),
    CONFLICT(409, "Conflict"),
    NOT_FOUND(404, "Not Found"),

    NO_MESSAGE_ID(BAD_REQUEST.code, "messageId can't be null!"),
    GENERIC_INVALID_PARAM(BAD_REQUEST.code, BAD_REQUEST.message),

    NO_FRANCHISE_FOUND(NOT_FOUND.code, "No franchise found with id"),
    NO_BRANCH_FOUND(NOT_FOUND.code, "No branch found with id"),
    NO_PRODUCT_FOUND(NOT_FOUND.code, "No product found with id"),
    NO_PRODUCTS_FOUND_IN_BRANCH(NOT_FOUND.code, "No products found in branch with id"),

    FRANCHISE_EXISTS(CONFLICT.code, "The franchise already exists with the name");

    private final Integer code;
    private final String message;

    @Override
    public BusinessException build() {
        return new BusinessException(this, this.message);
    }

    @Override
    public BusinessException build(String message) {
        return new BusinessException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message, message));
    }

    @Override
    public BusinessException build(String message, Throwable cause) {
        return new BusinessException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message,message), cause);
    }

    @Override
    public BusinessException build(Throwable cause) {
        return new BusinessException(this, String.format(EXCEPTION_MESSAGE.getFormat(), this.message,cause.getMessage()), cause);
    }

}
