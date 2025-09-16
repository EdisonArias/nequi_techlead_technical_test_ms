package co.com.nequi.teachlead.technical.test.mongo.product.repositories;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.mongo.helper.AdapterOperations;
import co.com.nequi.teachlead.technical.test.mongo.product.document.ProductDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductMongoAdapter extends AdapterOperations<Product, ProductDocument, String, ProductMongoRepository>
        implements ProductGateway {

    private final ReactiveMongoTemplate mongoTemplate;

    public ProductMongoAdapter(ProductMongoRepository repository,
                               ObjectMapper mapper,
                               ReactiveMongoTemplate mongoTemplate) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Product> saveProduct(Product product) {
        return save(product);
    }

    @Override
    public Mono<Product> getProductById(String productId) {
        return findById(productId);
    }

    @Override
    public Mono<Void> deleteProduct(String productId) {
        return deleteById(productId);
    }

    @Override
    public Flux<Product> getAllProducts() {
        var query = new Query().with(Sort.by(Sort.Order.asc("name")));
        return mongoTemplate.find(query, ProductDocument.class)
                .map(this::toEntity);
    }
}

