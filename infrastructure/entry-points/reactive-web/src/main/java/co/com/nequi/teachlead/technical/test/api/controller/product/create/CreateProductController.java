package co.com.nequi.teachlead.technical.test.api.controller.product.create;

import co.com.nequi.teachlead.technical.test.api.controller.product.create.mapper.ProductMapper;
import co.com.nequi.teachlead.technical.test.api.controller.product.create.request.CreateProduct;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.create.CreateProductUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.BRANCH_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductController {

    private final ValidateRequest validateRequest;
    private final CreateProductUseCase createProductUseCase;
    private final ProductMapper productMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String branchId = serverRequest.pathVariable(BRANCH_ID.getName());
        validateRequest.requireFranchiseId(branchId);

        return serverRequest.bodyToMono(CreateProduct.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .doOnNext(request -> {
                    log.info("Processing request to create product");
                    validateRequest.validate(request);
                })
                .map(productMapper::toEntity)
                .flatMap(product -> createProductUseCase.execute(branchId,product))
                .map(Response::build)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
