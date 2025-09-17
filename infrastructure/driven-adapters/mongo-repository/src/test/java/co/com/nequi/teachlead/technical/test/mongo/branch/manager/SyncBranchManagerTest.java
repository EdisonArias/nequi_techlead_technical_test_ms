package co.com.nequi.teachlead.technical.test.mongo.branch.manager;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SyncBranchManagerTest {

    @Mock
    ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    SyncBranchManager manager;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Captor
    ArgumentCaptor<Update> updateCaptor;

    @Test
    void updateBranchNameShouldReturnCountsWhenMatchedAndModified() {
        // Arrange
        String branchId = "64cb9f0a0a0a0a0a0a0a0a0a";
        String newName = "Sucursal Nueva";

        UpdateResult mongoResult = UpdateResult.acknowledged(2, 2L, (BsonValue) null);
        given(mongoTemplate.updateMulti(any(Query.class), any(Update.class), Mockito.eq(FranchiseDocument.class)))
                .willReturn(Mono.just(mongoResult));

        // Act
        Mono<UpdateCounts> result = manager.updateBranchName(branchId, newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(uc -> uc.matched() == 2 && uc.modified() == 2)
                .verifyComplete();

        verify(mongoTemplate).updateMulti(queryCaptor.capture(), updateCaptor.capture(), Mockito.eq(FranchiseDocument.class));
    }

    @Test
    void updateBranchNameShouldReturnZeroCountsWhenNoDocumentsMatched() {
        // Arrange
        String branchId = "64cb9f0a0a0a0a0a0a0a0a0a";
        String newName = "Sin Cambios";

        UpdateResult mongoResult = UpdateResult.acknowledged(0, 0L, (BsonValue) null);
        given(mongoTemplate.updateMulti(any(Query.class), any(Update.class), Mockito.eq(FranchiseDocument.class)))
                .willReturn(Mono.just(mongoResult));

        // Act
        Mono<UpdateCounts> result = manager.updateBranchName(branchId, newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(uc -> uc.matched() == 0 && uc.modified() == 0)
                .verifyComplete();
    }

    @Test
    void updateBranchNameShouldPropagateErrorWhenMongoFails() {
        // Arrange
        String branchId = "64cb9f0a0a0a0a0a0a0a0a0a";
        String newName = "ErrorName";

        given(mongoTemplate.updateMulti(any(Query.class), any(Update.class), Mockito.eq(FranchiseDocument.class)))
                .willReturn(Mono.error(new RuntimeException("mongo failure")));

        // Act & Assert
        StepVerifier.create(manager.updateBranchName(branchId, newName))
                .expectErrorMessage("mongo failure")
                .verify();
    }
}
