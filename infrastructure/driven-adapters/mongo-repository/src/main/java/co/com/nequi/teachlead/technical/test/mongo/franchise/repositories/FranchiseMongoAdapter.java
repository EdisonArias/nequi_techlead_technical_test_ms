package co.com.nequi.teachlead.technical.test.mongo.franchise.repositories;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
import co.com.nequi.teachlead.technical.test.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort;

@Repository
public class FranchiseMongoAdapter extends AdapterOperations<Franchise, FranchiseDocument, String, FranchiseMongoRepository>
        implements FranchiseGateway {

    private final ReactiveMongoTemplate mongoTemplate;

    public FranchiseMongoAdapter(FranchiseMongoRepository repository,
                             ObjectMapper mapper,
                             ReactiveMongoTemplate mongoTemplate) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Franchise> saveFranchise(Franchise franchise) {
        return save(franchise);
    }

    @Override
    public Mono<Franchise> getFranchiseById(String franchiseId) {
        return findById(franchiseId);
    }

    @Override
    public Flux<Franchise> getAllFranchises() {
        var query = new Query().with(Sort.by(Order.asc("name")));
        return mongoTemplate.find(query, FranchiseDocument.class)
                .map(this::toEntity);
    }

    @Override
    public Mono<Boolean> existsByName(String franchiseName) {
        return repository.existsByName(franchiseName).defaultIfEmpty(false);
    }
}

