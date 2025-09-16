package co.com.nequi.teachlead.technical.test.usecase.product.get;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
public class GetProductsUseCase {

    private final ProductGateway repository;

    public Flux<Product> execute() {
        return repository.getAllProducts();
    }
}
