package co.com.nequi.teachlead.technical.test.mongo.branch.repositories;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
import co.com.nequi.teachlead.technical.test.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class BranchMongoAdapter extends AdapterOperations<Branch, BranchDocument, String, BranchMongoRepository>
        implements BranchGateway {

    private final ReactiveMongoTemplate mongoTemplate;

    public BranchMongoAdapter(BranchMongoRepository repository,
                              ObjectMapper mapper,
                              ReactiveMongoTemplate mongoTemplate) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Branch> saveBranch(Branch branch) {
        return save(branch);
    }

    @Override
    public Mono<Branch> getBranchById(String branchId) {
        return findById(branchId);
    }

    @Override
    public Flux<Branch> getAllBranches() {
        var query = new Query().with(Sort.by(Sort.Order.asc("name")));
        return mongoTemplate.find(query, BranchDocument.class)
                .map(this::toEntity);
    }

}

