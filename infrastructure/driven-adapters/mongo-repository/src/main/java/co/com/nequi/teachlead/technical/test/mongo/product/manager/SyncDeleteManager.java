package co.com.nequi.teachlead.technical.test.mongo.product.manager;

import co.com.nequi.teachlead.technical.test.model.product.services.SyncDeleteProductService;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class SyncDeleteManager implements SyncDeleteProductService {

    private final ReactiveMongoTemplate mongo;

    private static final String PRODUCTS_ID = "products._id";
    private static final String COLLECTION = "products";
    private static final String PRODUCT_ID = "_id";

    @Override
    public Mono<Void> removeCategoryFromBranch(String productId) {
        var query = Query.query(Criteria.where(PRODUCTS_ID).is(new ObjectId(productId)));
        var update = new Update().pull(COLLECTION,
                Query.query(Criteria.where(PRODUCT_ID).is(new ObjectId(productId))));
        
        return mongo.updateMulti(query, update, BranchDocument.class)
                .then();
    }
}