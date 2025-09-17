package co.com.nequi.teachlead.technical.test.mongo.product.manager;

import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
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
class SyncDeleteManagerTest {

    @Mock
    ReactiveMongoTemplate mongo;

    @InjectMocks
    SyncDeleteManager manager;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Captor
    ArgumentCaptor<Update> updateCaptor;

    @Test
    void removeCategoryFromBranch() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0a";
        UpdateResult result = UpdateResult.acknowledged(2, 2L, (BsonValue) null);
        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.just(result));

        // Act
        Mono<Void> out = manager.removeCategoryFromBranch(productIdHex);

        // Assert
        StepVerifier.create(out).verifyComplete();

        verify(mongo).updateMulti(queryCaptor.capture(), updateCaptor.capture(), eq(BranchDocument.class));

        Document qDoc = queryCaptor.getValue().getQueryObject();
        ObjectId inQuery = (ObjectId) qDoc.get("products._id");
        assert inQuery != null;
        assert inQuery.toHexString().equals(productIdHex);

        Document upd = updateCaptor.getValue().getUpdateObject();
        Document pull = (Document) upd.get("$pull");
        assert pull != null : "Falta $pull";
        Object productsOperand = pull.get("products");
        assert productsOperand != null : "Falta 'products' en $pull";
    }

    @Test
    void removeCategoryFromBranchWhenNoDocumentsMatched() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0b";
        UpdateResult result = UpdateResult.acknowledged(0, 0L, (BsonValue) null);
        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.just(result));

        // Act & Assert
        StepVerifier.create(manager.removeCategoryFromBranch(productIdHex)).verifyComplete();
    }

    @Test
    void removeCategoryFromBranchWhenMongoFails() {
        // Arrange
        String productIdHex = "64cb9f0a0a0a0a0a0a0a0a0c";
        given(mongo.updateMulti(any(Query.class), any(Update.class), eq(BranchDocument.class)))
                .willReturn(Mono.error(new RuntimeException("db failure")));

        // Act & Assert
        StepVerifier.create(manager.removeCategoryFromBranch(productIdHex))
                .expectErrorMessage("db failure")
                .verify();
    }
}
