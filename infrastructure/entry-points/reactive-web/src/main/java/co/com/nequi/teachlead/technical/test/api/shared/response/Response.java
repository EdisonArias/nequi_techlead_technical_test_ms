package co.com.nequi.teachlead.technical.test.api.shared.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class Response<T> {

    private String code;
    private String message;
    private T data;

    private static final String SUCCESS = "SUCCESS";

    public static <T> Response<T> build(T data) {
        return Response.<T>builder()
                .code(String.valueOf(HttpStatus.OK.value()))
                .message(SUCCESS)
                .data(data)
                .build();
    }

    public static <T> Response<T> build(String code, String message, T data) {
        return Response.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }

}
