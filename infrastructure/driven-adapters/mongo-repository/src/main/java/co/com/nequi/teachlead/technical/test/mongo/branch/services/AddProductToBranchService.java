package co.com.nequi.teachlead.technical.test.mongo.branch.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.BranchToFranchise;
import co.com.nequi.teachlead.technical.test.model.branch.services.AddProductBranchService;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.services.AddBranchesFranchiseService;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.ProductToBranch;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalType;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
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
public class AddProductToBranchService implements AddProductBranchService {

    private static final Logger log = Logger.getLogger(AddProductToBranchService.class.getName());

    private final ReactiveMongoTemplate mongo;
    private final ObjectMapper mapper;

    private static final String ID = "id";
    private static final String PRODUCTS = "products";

    @Override
    public Mono<Branch> addProduct(String branchId, Product product) {

        return Mono.just(product)
                .map(this::toEmbeddedReference)
                .map(this::buildUpdate)
                .flatMap(update -> updateFranchise(branchId, update))
                .doOnNext(franchise -> log.info("Add product: " + product))
                .onErrorMap(e -> !(e instanceof BusinessException),
                        TechnicalType.MONGO_DB_ERROR::build);
    }

    private Update buildUpdate(ProductToBranch ref) {
        return new Update().addToSet(PRODUCTS).each(ref);
    }

    private Mono<Branch> updateFranchise(String franchiseId, Update update) {
        return mongo.findAndModify(
                        Query.query(Criteria.where(ID).is(franchiseId)),
                        update,
                        FindAndModifyOptions.options().returnNew(true),
                        BranchDocument.class)
                .map(this::toDomain)
                .switchIfEmpty(Mono.error(BusinessType.NO_BRANCH_FOUND.build(franchiseId)));
    }

    private Branch toDomain(BranchDocument doc) {
        return mapper.map(doc, Branch.class);
    }

    public ProductToBranch toEmbeddedReference(Product product) {
        return ProductToBranch.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .stock(product.getStock())
                        .build();
    }
}
