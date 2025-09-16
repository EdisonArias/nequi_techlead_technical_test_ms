package co.com.nequi.teachlead.technical.test.api.controller.product.get;

import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.usecase.product.get.GetProductsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetProductsController {

    private final ValidateRequest validateRequest;
    private final GetProductsUseCase getProductsUseCase;

    private static final String MESSAGE_GET_PRODUCTS_SUCCESS = "Successfully retrieved products {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String messageId = serverRequest.headers().firstHeader(Headers.MESSAGE_ID.getName());
        validateRequest.requireMessageId(messageId);

        return getProductsUseCase.execute()
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(messageId, brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_PRODUCTS_SUCCESS, response));
    }
}
