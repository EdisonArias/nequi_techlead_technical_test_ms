package co.com.nequi.teachlead.technical.test.mongo.franchise.services;

import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.mongo.franchise.helper.BranchTopProductProjection;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TopProductByBranchMongoServiceTest {

    @Mock
    ReactiveMongoTemplate mongo;

    @InjectMocks
    TopProductByBranchMongoService service;

    @Captor
    ArgumentCaptor<Aggregation> aggCaptor;

    private static final String COLLECTION = "branches";

    @Test
    void getTopProductForBranchShouldReturnMappedDomainWhenAggregateHasResult() {
        // Arrange
        String branchIdHex = "64cb9f0a0a0a0a0a0a0a0a0a";
        ObjectId branchId = new ObjectId(branchIdHex);
        ObjectId productId = new ObjectId("64cb9f0a0a0a0a0a0a0a0a0b");

        BranchTopProductProjection proj = new BranchTopProductProjection();
        proj.setBranchId(branchId);
        proj.setBranchName("Sucursal Centro");
        proj.setProductId(productId);
        proj.setProductName("Camiseta");
        proj.setStock(25);

        given(mongo.aggregate(any(Aggregation.class), eq(COLLECTION), eq(BranchTopProductProjection.class)))
                .willReturn(Flux.just(proj));

        // Act
        Mono<BranchTopProduct> result = service.getTopProductForBranch(branchIdHex);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(tp ->
                        tp.getBranchId().equals(branchIdHex) &&
                        tp.getBranchName().equals("Sucursal Centro") &&
                        tp.getProductId().equals(productId.toHexString()) &&
                        tp.getProductName().equals("Camiseta") &&
                        tp.getStock().equals(25)
                )
                .verifyComplete();

        verify(mongo).aggregate(aggCaptor.capture(), eq(COLLECTION), eq(BranchTopProductProjection.class));
        String pipelineStr = String.valueOf(aggCaptor.getValue());
        assert pipelineStr.contains("$match");
        assert pipelineStr.contains(branchIdHex);
        assert pipelineStr.contains("$unwind");
        assert pipelineStr.contains("products");
        assert pipelineStr.contains("$sort");
        assert pipelineStr.contains("products.stock");
        assert pipelineStr.contains("$limit");
        assert pipelineStr.contains("$project");
        assert pipelineStr.contains("branchId");
        assert pipelineStr.contains("productId");
        assert pipelineStr.contains("productName");
        assert pipelineStr.contains("stock");
    }

    @Test
    void getTopProductForBranchShouldReturnEmptyWhenAggregateReturnsNoResults() {
        // Arrange
        String branchIdHex = "64cb9f0a0a0a0a0a0a0a0a0a";

        given(mongo.aggregate(any(Aggregation.class), eq(COLLECTION), eq(BranchTopProductProjection.class)))
                .willReturn(Flux.empty()); // .next() → Mono.empty()

        // Act
        Mono<BranchTopProduct> result = service.getTopProductForBranch(branchIdHex);

        // Assert
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void getTopProductForBranchShouldPropagateErrorWhenAggregateFails() {
        // Arrange
        String branchIdHex = "64cb9f0a0a0a0a0a0a0a0a0a";

        given(mongo.aggregate(any(Aggregation.class), eq(COLLECTION), eq(BranchTopProductProjection.class)))
                .willReturn(Flux.error(new RuntimeException("aggregate failure")));

        // Act & Assert
        StepVerifier.create(service.getTopProductForBranch(branchIdHex))
                .expectErrorMessage("aggregate failure")
                .verify();
    }
}
