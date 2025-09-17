package co.com.nequi.teachlead.technical.test.mongo.franchise.services;

import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.ProductTopBranchGateway;
import co.com.nequi.teachlead.technical.test.mongo.franchise.helper.BranchTopProductProjection;
import co.com.nequi.teachlead.technical.test.mongo.franchise.mapper.BranchTopProductMapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class TopProductByBranchMongoService implements ProductTopBranchGateway {

    private final ReactiveMongoTemplate mongo;
    private static final String COLLECTION = "branches";

    @Override
    public Mono<BranchTopProduct> getTopProductForBranch(String branchId) {

        Aggregation pipeline = newAggregation(
                match(Criteria.where("_id").is(new ObjectId(branchId))),
                unwind("products"),
                sort(Sort.by(Sort.Direction.DESC, "products.stock")),
                limit(1),
                projectFields()
        );

        return mongo.aggregate(pipeline, COLLECTION, BranchTopProductProjection.class)
                .next()
                .map(BranchTopProductMapper::toDomain);
    }

    private ProjectionOperation projectFields() {
        return project()
                .and("_id").as("branchId")
                .and("name").as("branchName")
                .and("products._id").as("productId")
                .and("products.name").as("productName")
                .and("products.stock").as("stock");
    }
}
