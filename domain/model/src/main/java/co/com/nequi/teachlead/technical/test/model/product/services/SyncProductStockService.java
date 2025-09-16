package co.com.nequi.teachlead.technical.test.model.product.services;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import reactor.core.publisher.Mono;

public interface SyncProductStockService {
    Mono<UpdateCounts> updateProductStock(String productId, Integer newStock);
    Mono<UpdateCounts> updateProductName(String productId, String newName);
}

