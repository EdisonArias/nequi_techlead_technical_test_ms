package co.com.nequi.teachlead.technical.test.api.controller.product.delete;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.usecase.product.delete.DeleteProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.PRODUCT_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductController {

    private final ValidateRequest validateRequest;
    private final DeleteProductUseCase deleteProductUseCase;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String productId = serverRequest.pathVariable(PRODUCT_ID.getName());
        validateRequest.requireFranchiseId(productId);

        return this.deleteProductUseCase.execute(productId)
                .thenReturn(Response.build(null))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
}
