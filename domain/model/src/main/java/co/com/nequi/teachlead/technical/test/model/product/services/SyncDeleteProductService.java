package co.com.nequi.teachlead.technical.test.model.product.services;

import reactor.core.publisher.Mono;

public interface SyncDeleteProductService {
    Mono<Void> removeCategoryFromBranch(String productId);
}

