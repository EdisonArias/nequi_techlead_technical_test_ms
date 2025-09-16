package co.com.nequi.teachlead.technical.test.usecase.product.update.name;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.model.product.services.SyncProductStockService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {

    private static final Logger log = Logger.getLogger(UpdateProductNameUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_FRANCHISE_UPDATED = "Update Product Name: ";

    private final ProductGateway repository;
    private final SyncProductStockService syncProductStockService;

    public Mono<Product> execute(Product newProduct, String productId) {
        return this.repository.getProductById(productId)
                .map(productFound -> buildUpdateProductName(productFound,newProduct.getName()))
                .flatMap(repository::saveProduct)
                .flatMap(saved -> syncProductNameInBranch(saved, newProduct))
                .doOnNext(result -> log.info(MESSAGE_FRANCHISE_UPDATED + result))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.NO_PRODUCT_FOUND.build(productId))));
    }

    private Product buildUpdateProductName(Product product , String newName) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return product.toBuilder()
                .name(newName)
                .modificationDate(now.format(formatter))
                .build();
    }

    private Mono<Product> syncProductNameInBranch(Product saved, Product newProduct) {
        return syncProductStockService.updateProductName(saved.getId(), newProduct.getName())
                .thenReturn(saved);
    }
}
