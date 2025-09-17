package co.com.nequi.teachlead.technical.test.mongo.franchise.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalType;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
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
class AddBranchesToFranchiseServiceTest {

    @Mock
    ReactiveMongoTemplate mongo;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    AddBranchesToFranchiseService service;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Captor
    ArgumentCaptor<Update> updateCaptor;

    @Captor
    ArgumentCaptor<FindAndModifyOptions> famCaptor;

    @Test
    void addBranchShouldEmbedAndReturnFranchise() {
        // Arrange
        String franchiseId = "F1";
        Branch branch = Branch.builder()
                .id("B1")
                .name("Sucursal Centro")
                .build();

        FranchiseDocument updatedDoc = new FranchiseDocument();
        updatedDoc.setId(franchiseId);
        updatedDoc.setName("Franquicia X");

        Franchise mapped = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia X")
                .build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranchiseDocument.class)))
                .willReturn(Mono.just(updatedDoc));
        given(mapper.map(updatedDoc, Franchise.class)).willReturn(mapped);

        // Act
        Mono<Franchise> result = service.addBranch(franchiseId, branch);

        // Assert
        StepVerifier.create(result)
                .expectNext(mapped)
                .verifyComplete();

        verify(mongo).findAndModify(queryCaptor.capture(), updateCaptor.capture(), famCaptor.capture(), eq(FranchiseDocument.class));

        Document qDoc = queryCaptor.getValue().getQueryObject();
        assert franchiseId.equals(qDoc.getString("id"));

        assert famCaptor.getValue().isReturnNew();

        Document upd = updateCaptor.getValue().getUpdateObject();
        Document addToSet = (Document) upd.get("$addToSet");
        assert addToSet != null : "Falta $addToSet";
        Object branchesOperand = addToSet.get("branches");
        assert branchesOperand != null : "Falta branches en $addToSet";
    }

    @Test
    void addBranchShouldErrorWhenFranchiseNotFound() {
        // Arrange
        String franchiseId = "NOPE";
        Branch branch = Branch.builder().id("B9").name("Norte").build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranchiseDocument.class)))
                .willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.addBranch(franchiseId, branch))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_FRANCHISE_FOUND)
                .verify();
    }

    @Test
    void addBranchShouldMapToTechnicalErrorWhenMongoFails() {
        // Arrange
        String franchiseId = "F2";
        Branch branch = Branch.builder().id("B2").name("Occidente").build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(FranchiseDocument.class)))
                .willReturn(Mono.error(new RuntimeException("db failure")));

        // Act & Assert
        StepVerifier.create(service.addBranch(franchiseId, branch))
                .expectErrorMatches(ex -> ex instanceof TechnicalException &&
                        ((TechnicalException) ex).getType() == TechnicalType.MONGO_DB_ERROR)
                .verify();
    }
}
