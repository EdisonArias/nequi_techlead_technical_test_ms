package co.com.nequi.teachlead.technical.test.mongo.product.manager;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import co.com.nequi.teachlead.technical.test.model.product.services.SyncProductStockService;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncProductStockManager implements SyncProductStockService {
    private final ReactiveMongoTemplate mongo;

    private static final String FIELD = "products._id";
    private static final String ID_PRODUCT = "elem._id";
    private static final String STOCK_PRODUCT_IN_BRANCH = "products.$[elem].stock";
    private static final String NAME_PRODUCT_IN_BRANCH = "products.$[elem].name";

    public Mono<UpdateCounts> updateProductStock(String branchId, Integer newStock) {

        ObjectId catObjectId = new ObjectId(branchId);

        Query query = Query.query(Criteria.where(FIELD).is(catObjectId));

        Update update = new Update()
                .filterArray(Criteria.where(ID_PRODUCT).is(catObjectId))
                .set(STOCK_PRODUCT_IN_BRANCH, newStock);

        return mongo.updateMulti(query, update, BranchDocument.class)
                .map(this::toDomainCounts)
                .doOnNext(updateCounts -> log.info("updated product stock: {}",updateCounts));
    }

    public Mono<UpdateCounts> updateProductName(String branchId, String newName) {

        ObjectId catObjectId = new ObjectId(branchId);

        Query query = Query.query(Criteria.where(FIELD).is(catObjectId));

        Update update = new Update()
                .filterArray(Criteria.where(ID_PRODUCT).is(catObjectId))
                .set(NAME_PRODUCT_IN_BRANCH, newName);

        return mongo.updateMulti(query, update, BranchDocument.class)
                .map(this::toDomainCounts)
                .doOnNext(updateCounts -> log.info("updated product name: {}",updateCounts));
    }

    private UpdateCounts toDomainCounts(UpdateResult r) {
        return new UpdateCounts(r.getMatchedCount(), r.getModifiedCount());
    }
}

