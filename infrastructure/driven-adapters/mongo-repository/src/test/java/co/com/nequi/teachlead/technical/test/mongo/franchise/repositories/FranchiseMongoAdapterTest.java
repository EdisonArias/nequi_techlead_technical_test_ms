package co.com.nequi.teachlead.technical.test.mongo.franchise.repositories;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.mongo.franchise.document.FranchiseDocument;
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
class FranchiseMongoAdapterTest {

    @Mock
    FranchiseMongoRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    FranchiseMongoAdapter adapter;

    @Test
    void saveFranchiseShouldPersistAndReturnMappedDomain() {
        // Arrange
        Franchise domainIn = Franchise.builder().name("KFC").build();

        FranchiseDocument docToSave = new FranchiseDocument();
        docToSave.setName("KFC");

        FranchiseDocument docSaved = new FranchiseDocument();
        docSaved.setId("F1"); docSaved.setName("KFC");

        Franchise domainOut = Franchise.builder().id("F1").name("KFC").build();

        given(mapper.map(domainIn, FranchiseDocument.class)).willReturn(docToSave);
        given(repository.save(docToSave)).willReturn(Mono.just(docSaved));
        given(mapper.map(docSaved, Franchise.class)).willReturn(domainOut);

        // Act
        Mono<Franchise> result = adapter.saveFranchise(domainIn);

        // Assert
        StepVerifier.create(result)
                .expectNext(domainOut)
                .verifyComplete();
    }


    @Test
    void getFranchiseById_ShouldReturnMappedDomain_WhenExists() {
        // Arrange
        String id = "F1";
        FranchiseDocument doc = new FranchiseDocument();
        doc.setId(id); doc.setName("Burger King");

        Franchise domain = Franchise.builder().id(id).name("Burger King").build();

        given(repository.findById(id)).willReturn(Mono.just(doc));
        given(mapper.map(doc, Franchise.class)).willReturn(domain);

        // Act
        Mono<Franchise> result = adapter.getFranchiseById(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void getFranchiseById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        given(repository.findById("NOPE")).willReturn(Mono.empty());

        // Act
        Mono<Franchise> result = adapter.getFranchiseById("NOPE");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllFranchisesShouldReturnSortedByNameAsc() {
        // Arrange
        FranchiseDocument d1 = new FranchiseDocument(); d1.setId("F1"); d1.setName("KFC");
        FranchiseDocument d2 = new FranchiseDocument(); d2.setId("F2"); d2.setName("McD");

        Franchise f1 = Franchise.builder().id("F1").name("KFC").build();
        Franchise f2 = Franchise.builder().id("F2").name("McD").build();

        given(mongoTemplate.find(any(Query.class), eq(FranchiseDocument.class)))
                .willAnswer(inv -> {
                    Query q = inv.getArgument(0);
                    Document sortDoc = q.getSortObject();
                    assert sortDoc.getInteger("name", 0) == 1;
                    return Flux.just(d1, d2);
                });

        given(mapper.map(d1, Franchise.class)).willReturn(f1);
        given(mapper.map(d2, Franchise.class)).willReturn(f2);

        // Act
        Flux<Franchise> result = adapter.getAllFranchises();

        // Assert
        StepVerifier.create(result)
                .expectNext(f1, f2)
                .verifyComplete();
    }

    @Test
    void getAllFranchisesShouldReturnEmptyWhenNoDocs() {
        // Arrange
        given(mongoTemplate.find(any(Query.class), eq(FranchiseDocument.class)))
                .willReturn(Flux.empty());

        // Act
        Flux<Franchise> result = adapter.getAllFranchises();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void getAllFranchisesShouldPropagateErrorWhenMongoFails() {
        // Arrange
        given(mongoTemplate.find(any(Query.class), eq(FranchiseDocument.class)))
                .willReturn(Flux.error(new RuntimeException("mongo error")));

        // Act
        Flux<Franchise> result = adapter.getAllFranchises();

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("mongo error")
                .verify();
    }

    @Test
    void existsByNameShouldReturnTrue() {
        // Arrange
        given(repository.existsByName("KFC")).willReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = adapter.existsByName("KFC");

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByNameShouldReturnFalse() {
        // Arrange
        given(repository.existsByName("KFC")).willReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = adapter.existsByName("KFC");

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsByNameShouldReturnFalseRepositoryReturnsEmpty() {
        // Arrange
        given(repository.existsByName("Unknown")).willReturn(Mono.empty());

        // Act
        Mono<Boolean> result = adapter.existsByName("Unknown");

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
