package co.com.nequi.teachlead.technical.test.usecase.product.update.stock;

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
public class UpdateProductStockUseCase {

    private static final Logger log = Logger.getLogger(UpdateProductStockUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_FRANCHISE_UPDATED = "Update Product Stock: ";

    private final ProductGateway repository;
    private final SyncProductStockService syncProductStockService;

    public Mono<Product> execute(Product newProduct, String productId) {
        return this.repository.getProductById(productId)
                .map(productFound -> buildUpdateProductStock(productFound,newProduct.getStock()))
                .flatMap(repository::saveProduct)
                .flatMap(saved -> syncProductStockInBranch(saved, newProduct))
                .doOnNext(result -> log.info(MESSAGE_FRANCHISE_UPDATED + result))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.NO_PRODUCT_FOUND.build(productId))));
    }

    private Product buildUpdateProductStock(Product product , Integer newStock) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return product.toBuilder()
                .stock(newStock)
                .modificationDate(now.format(formatter))
                .build();
    }

    private Mono<Product> syncProductStockInBranch(Product saved, Product newProduct) {
        return syncProductStockService.updateProductStock(saved.getId(), newProduct.getStock())
                .thenReturn(saved);
    }
}
