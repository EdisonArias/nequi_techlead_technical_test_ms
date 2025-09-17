package co.com.nequi.teachlead.technical.test.mongo.branch.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.TechnicalType;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
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
class AddProductToBranchServiceTest {

    @Mock
    ReactiveMongoTemplate mongo;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    AddProductToBranchService service;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @Captor
    ArgumentCaptor<Update> updateCaptor;

    @Captor
    ArgumentCaptor<FindAndModifyOptions> famCaptor;

    @Test
    void addProductShouldEmbedAndReturnBranchWhenBranchExists() {
        // Arrange
        String branchId = "B1";
        Product product = Product.builder()
                .id("P1")
                .name("Zapatos")
                .stock(15)
                .build();

        BranchDocument updatedDoc = new BranchDocument();
        updatedDoc.setId(branchId);
        updatedDoc.setName("Sucursal Centro");

        Branch mapped = Branch.builder()
                .id(branchId)
                .name("Sucursal Centro")
                .build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(BranchDocument.class)))
                .willReturn(Mono.just(updatedDoc));
        given(mapper.map(updatedDoc, Branch.class)).willReturn(mapped);

        // Act
        Mono<Branch> result = service.addProduct(branchId, product);

        // Assert
        StepVerifier.create(result)
                .expectNext(mapped)
                .verifyComplete();

        verify(mongo).findAndModify(queryCaptor.capture(), updateCaptor.capture(), famCaptor.capture(), eq(BranchDocument.class));

        Document qDoc = queryCaptor.getValue().getQueryObject();
        assert branchId.equals(qDoc.getString("id"));

        FindAndModifyOptions opts = famCaptor.getValue();
        assert opts.isReturnNew();
    }

    @Test
    void addProductShouldErrorWhenBranchNotFound() {
        // Arrange
        String branchId = "NOPE";
        Product product = Product.builder().id("P9").name("Gorra").stock(5).build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(BranchDocument.class)))
                .willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.addProduct(branchId, product))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_BRANCH_FOUND)
                .verify();
    }

    @Test
    void addProductShouldMapToTechnicalErrorWhenMongoFails() {
        // Arrange
        String branchId = "B2";
        Product product = Product.builder().id("P2").name("Camiseta").stock(8).build();

        given(mongo.findAndModify(any(Query.class), any(Update.class), any(FindAndModifyOptions.class), eq(BranchDocument.class)))
                .willReturn(Mono.error(new RuntimeException("db failure")));

        // Act & Assert
        StepVerifier.create(service.addProduct(branchId, product))
                .expectErrorMatches(ex -> ex instanceof TechnicalException &&
                        ((TechnicalException) ex).getType() == TechnicalType.MONGO_DB_ERROR)
                .verify();
    }
}
