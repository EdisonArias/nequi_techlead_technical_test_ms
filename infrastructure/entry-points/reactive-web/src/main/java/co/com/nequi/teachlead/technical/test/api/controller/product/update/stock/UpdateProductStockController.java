package co.com.nequi.teachlead.technical.test.api.controller.product.update.stock;

import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.mapper.ProductStockMapper;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.request.UpdateProductStock;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.update.stock.UpdateProductStockUseCase;
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
public class UpdateProductStockController {

    private final ValidateRequest validateRequest;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final ProductStockMapper productStockMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String productId = serverRequest.pathVariable(PRODUCT_ID.getName());
        validateRequest.requireFranchiseId(productId);

        return serverRequest.bodyToMono(UpdateProductStock.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .doOnNext(request -> {
                    log.info("Processing request to update product stock");
                    validateRequest.validate(request);
                })
                .map(productStockMapper::toEntity)
                .flatMap(product -> updateProductStockUseCase.execute(product,productId))
                .map(Response::build)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
