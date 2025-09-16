package co.com.nequi.teachlead.technical.test.mongo.franchise.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.BranchToFranchise;
import co.com.nequi.teachlead.technical.test.model.franchise.services.AddBranchesFranchiseService;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalType;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AddBranchesToFranchiseService implements AddBranchesFranchiseService {

    private static final Logger log = Logger.getLogger(AddBranchesToFranchiseService.class.getName());

    private final ReactiveMongoTemplate mongo;
    private final ObjectMapper mapper;

    private static final String ID = "id";
    private static final String BRANCHES = "branches";

    @Override
    public Mono<Franchise> addBranch(String franchiseId, Branch branch) {

        return Mono.just(branch)
                .map(this::toEmbeddedReference)
                .map(this::buildUpdate)
                .flatMap(update -> updateFranchise(franchiseId, update))
                .doOnNext(franchise -> log.info("Add branch: " + branch))
                .onErrorMap(e -> !(e instanceof BusinessException),
                        TechnicalType.MONGO_DB_ERROR::build);
    }

    private Update buildUpdate(BranchToFranchise ref) {
        return new Update().addToSet(BRANCHES).each(ref);
    }

    private Mono<Franchise> updateFranchise(String franchiseId, Update update) {
        return mongo.findAndModify(
                        Query.query(Criteria.where(ID).is(franchiseId)),
                        update,
                        FindAndModifyOptions.options().returnNew(true),
                        FranchiseDocument.class)
                .map(this::toDomain)
                .switchIfEmpty(Mono.error(BusinessType.NO_FRANCHISE_FOUND.build(franchiseId)));
    }

    private Franchise toDomain(FranchiseDocument doc) {
        return mapper.map(doc, Franchise.class);
    }

    public BranchToFranchise toEmbeddedReference(Branch branch) {
        return BranchToFranchise.builder()
                        .id(branch.getId())
                        .name(branch.getName())
                        .build();
    }
}
