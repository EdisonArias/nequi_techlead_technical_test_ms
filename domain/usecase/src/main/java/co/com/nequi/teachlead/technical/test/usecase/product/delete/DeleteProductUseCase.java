package co.com.nequi.teachlead.technical.test.usecase.product.delete;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.model.product.services.SyncDeleteProductService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class DeleteProductUseCase {

    private static final Logger log = Logger.getLogger(DeleteProductUseCase.class.getName());

    private final ProductGateway repository;
    private final SyncDeleteProductService syncDeleteProductService;

    public Mono<Void> execute(String productId) {
        return checkProductExists(productId)
                .flatMap(this::removeProduct)
                .doOnSuccess(unused -> log.info("Deleted Category: " + productId));
    }

    public Mono<Product> checkProductExists(String productId) {
        return repository.getProductById(productId)
                .switchIfEmpty(Mono.defer(() -> Mono.error(BusinessType.NO_PRODUCT_FOUND.build(productId))));
    }

    private Mono<Void> removeProduct(Product product) {
        return Mono.zip(
                syncDeleteProductService.removeCategoryFromBranch(product.getId()),
                repository.deleteProduct(product.getId()))
                .then();
    }
}
