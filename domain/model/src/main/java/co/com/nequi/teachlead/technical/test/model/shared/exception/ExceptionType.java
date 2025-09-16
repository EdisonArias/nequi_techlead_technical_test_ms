package co.com.nequi.teachlead.technical.test.model.shared.exception;

public interface ExceptionType {
    Integer getCode();
    String getMessage();

    AppException build();
    AppException build(String message);
    AppException build(String message, Throwable cause);
    AppException build(Throwable cause);
}
