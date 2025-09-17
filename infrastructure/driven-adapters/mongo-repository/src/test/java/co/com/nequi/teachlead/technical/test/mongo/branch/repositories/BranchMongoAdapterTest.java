package co.com.nequi.teachlead.technical.test.mongo.branch.repositories;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class BranchMongoAdapterTest {

    @Mock
    BranchMongoRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    BranchMongoAdapter adapter;

    @Captor
    ArgumentCaptor<Query> queryCaptor;


    @Test
    void saveBranchShouldPersistAndReturnMappedDomain() {
        // Arrange
        Branch domainIn = Branch.builder().name("Bogota").build();
        BranchDocument docSaved = new BranchDocument();
        docSaved.setId("B1"); docSaved.setName("Bogota");

        Branch domainOut = Branch.builder().id("B1").name("Bogota").build();

        given(mapper.map(domainIn, BranchDocument.class)).willReturn(docSaved);
        given(repository.save(docSaved)).willReturn(Mono.just(docSaved));
        given(mapper.map(docSaved, Branch.class)).willReturn(domainOut);

        // Act
        Mono<Branch> result = adapter.saveBranch(domainIn);

        // Assert
        StepVerifier.create(result)
                .expectNext(domainOut)
                .verifyComplete();
    }

    @Test
    void getBranchByIdShouldReturnMappedDomainWhenExists() {
        // Arrange
        String id = "B1";
        BranchDocument doc = new BranchDocument();
        doc.setId(id); doc.setName("Medellin");
        Branch domain = Branch.builder().id(id).name("Medellin").build();

        given(repository.findById(id)).willReturn(Mono.just(doc));
        given(mapper.map(doc, Branch.class)).willReturn(domain);

        // Act
        Mono<Branch> result = adapter.getBranchById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void getBranchByIdShouldReturnEmptyWhenNotFound() {
        // Arrange
        given(repository.findById("NOPE")).willReturn(Mono.empty());

        // Act
        Mono<Branch> result = adapter.getBranchById("NOPE");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllBranchesShouldReturnSortedByNameAsc() {
        // Arrange
        BranchDocument d1 = new BranchDocument(); d1.setId("B1"); d1.setName("Bogota");
        BranchDocument d2 = new BranchDocument(); d2.setId("B2"); d2.setName("Cali");

        Branch b1 = Branch.builder().id("B1").name("Bogota").build();
        Branch b2 = Branch.builder().id("B2").name("Cali").build();

        given(mongoTemplate.find(any(Query.class), eq(BranchDocument.class)))
                .willAnswer(inv -> {
                    Query q = inv.getArgument(0);
                    Document sortDoc = q.getSortObject();
                    assert sortDoc.getInteger("name", 0) == 1;
                    return Flux.just(d1, d2);
                });

        given(mapper.map(d1, Branch.class)).willReturn(b1);
        given(mapper.map(d2, Branch.class)).willReturn(b2);

        // Act
        Flux<Branch> result = adapter.getAllBranches();

        // Assert
        StepVerifier.create(result)
                .expectNext(b1, b2)
                .verifyComplete();
    }

    @Test
    void getAllBranchesShouldReturnEmptyWhenNoDocs() {
        // Arrange
        given(mongoTemplate.find(any(Query.class), eq(BranchDocument.class)))
                .willReturn(Flux.empty());

        // Act
        Flux<Branch> result = adapter.getAllBranches();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllBranchesShouldPropagateErrorWhenMongoFails() {
        // Arrange
        given(mongoTemplate.find(any(Query.class), eq(BranchDocument.class)))
                .willReturn(Flux.error(new RuntimeException("mongo error")));

        // Act
        Flux<Branch> result = adapter.getAllBranches();

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("mongo error")
                .verify();
    }
}
