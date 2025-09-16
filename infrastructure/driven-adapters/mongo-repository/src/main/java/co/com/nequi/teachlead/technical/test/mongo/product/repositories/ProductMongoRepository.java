package co.com.nequi.teachlead.technical.test.mongo.product.repositories;

import co.com.nequi.teachlead.technical.test.mongo.product.document.ProductDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String>,
        ReactiveQueryByExampleExecutor<ProductDocument> {

    Mono<Boolean> existsByName(String franchiseName);
}
