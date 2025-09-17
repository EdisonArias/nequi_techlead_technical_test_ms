package co.com.nequi.teachlead.technical.test.api.controller.product.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.usecase.product.get.GetTopProductByBranchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.FRANCHISE_ID;


@Slf4j
@Service
@RequiredArgsConstructor
public class GetTopProductsInBranchController {

    private final ValidateRequest validateRequest;
    private final GetTopProductByBranchUseCase getTopProductByBranchUseCase;

    private static final String MESSAGE_GET_TOP_PRODUCTS_SUCCESS = "Successfully retrieved top products {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable(FRANCHISE_ID.getName());
        validateRequest.requireFranchiseId(franchiseId);

        return getTopProductByBranchUseCase.execute(franchiseId)
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_TOP_PRODUCTS_SUCCESS, response));
    }
}
