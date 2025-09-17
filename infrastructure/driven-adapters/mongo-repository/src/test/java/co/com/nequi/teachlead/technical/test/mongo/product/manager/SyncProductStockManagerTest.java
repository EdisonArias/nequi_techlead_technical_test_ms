package co.com.nequi.teachlead.technical.test.mongo.product.manager;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SyncProductStockManagerTest {

    @Mock
    ReactiveMongoTemplate mongo;

    @InjectMocks
    SyncProductStockManager manager;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Captor
    ArgumentCaptor<Update> updateCaptor;

    @Test
    void updateProductStockAndUpdate() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0a";
        int newStock = 42;

        UpdateResult result = UpdateResult.acknowledged(3, 2L, (BsonValue) null);
        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.just(result));

        // Act
        Mono<UpdateCounts> out = manager.updateProductStock(productIdHex, newStock);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(uc -> uc.matched() == 3 && uc.modified() == 2)
                .verifyComplete();

        verify(mongo).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(BranchDocument.class));
    }

    @Test
    void updateProductStockShouldPropagateErrorWhenMongoFails() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0b";

        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.error(new RuntimeException("db failure")));

        // Act & Assert
        StepVerifier.create(manager.updateProductStock(productIdHex, 10))
                .expectErrorMessage("db failure")
                .verify();
    }


    @Test
    void updateProductNameAndUpdate() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0c";
        String newName = "Nuevo Nombre";

        UpdateResult result = UpdateResult.acknowledged(5, 4L, (BsonValue) null);
        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.just(result));

        // Act
        Mono<UpdateCounts> out = manager.updateProductName(productIdHex, newName);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(uc -> uc.matched() == 5 && uc.modified() == 4)
                .verifyComplete();

        verify(mongo).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(BranchDocument.class));

    }

    @Test
    void updateProductName_ShouldPropagateError_WhenMongoFails() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0d";

        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.error(new RuntimeException("db failure")));

        // Act & Assert
        StepVerifier.create(manager.updateProductName(productIdHex, "X"))
                .expectErrorMessage("db failure")
                .verify();
    }
}
