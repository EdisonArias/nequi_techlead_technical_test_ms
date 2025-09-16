package co.com.nequi.teachlead.technical.test.mongo.branch.manager;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import co.com.nequi.teachlead.technical.test.model.branch.services.SyncBranchService;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
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
public class SyncBranchManager implements SyncBranchService {
    private final ReactiveMongoTemplate mongo;

    private static final String FIELD = "branches._id";
    private static final String ID_BRANCH = "elem._id";
    private static final String ID_BRANCH_IN_BRAND = "branches.$[elem].name";

    public Mono<UpdateCounts> updateBranchName(String branchId, String newName) {

        ObjectId catObjectId = new ObjectId(branchId);

        Query query = Query.query(Criteria.where(FIELD).is(catObjectId));

        Update update = new Update()
                .filterArray(Criteria.where(ID_BRANCH).is(catObjectId))
                .set(ID_BRANCH_IN_BRAND, newName);

        return mongo.updateMulti(query, update, FranchiseDocument.class)
                .map(this::toDomainCounts)
                .doOnNext(updateCounts -> log.info("updated franchises: {}",updateCounts));
    }

    private UpdateCounts toDomainCounts(UpdateResult r) {
        return new UpdateCounts(r.getMatchedCount(), r.getModifiedCount());
    }
}

