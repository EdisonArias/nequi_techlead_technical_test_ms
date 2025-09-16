package co.com.nequi.teachlead.technical.test.api.controller.product.update.name;

import co.com.nequi.teachlead.technical.test.api.controller.product.update.name.mapper.ProductNameMapper;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.name.request.UpdateProductName;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.update.name.UpdateProductNameUseCase;
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
public class UpdateProductNameController {

    private final ValidateRequest validateRequest;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final ProductNameMapper productNameMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String productId = serverRequest.pathVariable(PRODUCT_ID.getName());
        validateRequest.requireFranchiseId(productId);

        return serverRequest.bodyToMono(UpdateProductName.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .doOnNext(request -> {
                    log.info("Processing request to update product name");
                    validateRequest.validate(request);
                })
                .map(productNameMapper::toEntity)
                .flatMap(product -> updateProductNameUseCase.execute(product,productId))
                .map(franchise -> Response.build(productId, franchise))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
