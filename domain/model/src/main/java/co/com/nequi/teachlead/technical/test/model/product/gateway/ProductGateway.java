package co.com.nequi.teachlead.technical.test.model.product.gateway;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductGateway {

    Mono<Product> saveProduct(Product product);
    Mono<Product> getProductById(String brandId);
    Mono<Void> deleteProduct(String productId);
    Flux<Product> getAllProducts();
}
