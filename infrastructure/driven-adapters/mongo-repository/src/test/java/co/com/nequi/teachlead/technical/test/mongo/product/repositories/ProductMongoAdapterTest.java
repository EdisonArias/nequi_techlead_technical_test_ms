package co.com.nequi.teachlead.technical.test.mongo.product.repositories;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.mongo.product.document.ProductDocument;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductMongoAdapterTest {

    @Mock
    ProductMongoRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    ReactiveMongoTemplate mongoTemplate;

    @Captor
    ArgumentCaptor<Query> queryCaptor;

    @InjectMocks
    ProductMongoAdapter adapter;


    @Test
    void getAllProductsProductsSortedByNameAsc() {
        // Arrange
        ProductDocument d1 = new ProductDocument();
        d1.setId("p1"); d1.setName("A"); d1.setStock(10);
        ProductDocument d2 = new ProductDocument();
        d2.setId("p2"); d2.setName("Z"); d2.setStock(5);

        Product p1 = Product.builder().id("p1").name("A").stock(10).build();
        Product p2 = Product.builder().id("p2").name("Z").stock(5).build();

        given(mongoTemplate.find(any(Query.class), eq(ProductDocument.class)))
                .willAnswer(inv -> {
                    Query q = inv.getArgument(0);
                    Document sortBson = q.getSortObject();
                    assert sortBson.getInteger("name") == 1;
                    return Flux.just(d1, d2);
                });

        given(mapper.map(d1, Product.class)).willReturn(p1);
        given(mapper.map(d2, Product.class)).willReturn(p2);

        // Act
        Flux<Product> out = adapter.getAllProducts();

        // Assert
        StepVerifier.create(out)
                .expectNext(p1, p2)
                .verifyComplete();

        verify(mongoTemplate).find(queryCaptor.capture(), eq(ProductDocument.class));
        Document sortDoc = queryCaptor.getValue().getSortObject();
        assert sortDoc.getInteger("name") == 1;
    }


    @Test
    void getProductById_ShouldReturnProduct_WhenExists() {
        // Arrange
        String id = "p-123";
        ProductDocument doc = new ProductDocument();
        doc.setId(id); doc.setName("Camisa"); doc.setStock(7);

        Product expected = Product.builder().id(id).name("Camisa").stock(7).build();

        given(repository.findById(id)).willReturn(Mono.just(doc));
        given(mapper.map(doc, Product.class)).willReturn(expected);

        // Act
        Mono<Product> out = adapter.getProductById(id);

        // Assert
        StepVerifier.create(out)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    void getProductByIdWhenNotFound() {
        // Arrange
        String id = "missing";
        given(repository.findById(id)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.getProductById(id)).verifyComplete();
    }

    @Test
    void saveProductReturnMappedDomain() {
        // Arrange
        Product toSave = Product.builder().name("Gorra").stock(3).build();

        ProductDocument docIn = new ProductDocument();
        docIn.setName("Gorra"); docIn.setStock(3);

        ProductDocument docSaved = new ProductDocument();
        docSaved.setId("gen-1"); docSaved.setName("Gorra"); docSaved.setStock(3);

        Product expected = Product.builder().id("gen-1").name("Gorra").stock(3).build();

        given(mapper.map(toSave, ProductDocument.class)).willReturn(docIn);
        given(repository.save(docIn)).willReturn(Mono.just(docSaved));
        given(mapper.map(docSaved, Product.class)).willReturn(expected);

        // Act
        Mono<Product> out = adapter.saveProduct(toSave);

        // Assert
        StepVerifier.create(out)
                .expectNext(expected)
                .verifyComplete();
    }


    @Test
    void deleteProductShouldComplete() {
        // Arrange
        String id = "p-del";
        given(repository.deleteById(id)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.deleteProduct(id)).verifyComplete();
        verify(repository).deleteById(id);
    }
}
