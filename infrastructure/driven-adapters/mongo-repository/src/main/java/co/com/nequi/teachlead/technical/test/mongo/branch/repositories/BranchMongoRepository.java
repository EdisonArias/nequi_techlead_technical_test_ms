package co.com.nequi.teachlead.technical.test.mongo.branch.repositories;

import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import reactor.core.publisher.Mono;

public interface BranchMongoRepository extends ReactiveMongoRepository<BranchDocument, String>,
        ReactiveQueryByExampleExecutor<BranchDocument> {

    Mono<Boolean> existsByName(String franchiseName);

}
