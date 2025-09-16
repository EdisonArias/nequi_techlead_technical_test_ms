package co.com.nequi.teachlead.technical.test.api.shared.exception;

import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.shared.exception.AppException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  WebProperties webProperties,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer codecConfigurator) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        setMessageReaders(codecConfigurator.getReaders());
        setMessageWriters(codecConfigurator.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleErrorResponse);
    }

    private Mono<ServerResponse> handleErrorResponse(ServerRequest request) {
        Throwable t = getError(request);
        String messageId = request.headers().firstHeader(Headers.MESSAGE_ID.getName());
        int status = resolveHttpStatus(t);

        Response<String> body = buildBody(t, messageId, status);

        log.error("{}, type: {} , messageId: {} , cause: {} :: ",
                t.getClass().getSimpleName(),
                (t instanceof AppException e) ? e.getType() : TechnicalType.INTERNAL_SERVER_ERROR,
                messageId,
                t.getMessage(),
                t);

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body);
    }

    private int resolveHttpStatus(Throwable t) {
        return switch (t) {
            case AppException e -> e.getType().getCode();
            case ErrorResponseException e -> e.getStatusCode().value();
            default -> HttpStatus.INTERNAL_SERVER_ERROR.value();
        };
    }

    private Response<String> buildBody(Throwable t, String messageId, int status) {
        String code;
        String message;

        if (t instanceof AppException e) {
            code = e.getType().getCode().toString();
            message = t.getMessage();
        } else {
            code = String.valueOf(status);
            message = TechnicalType.INTERNAL_SERVER_ERROR.getMessage();
        }

        return Response.build(messageId, code, message, null);
    }
}
