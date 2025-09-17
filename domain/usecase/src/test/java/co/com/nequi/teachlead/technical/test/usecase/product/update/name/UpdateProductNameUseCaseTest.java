package co.com.nequi.teachlead.technical.test.usecase.product.update.name;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.model.product.services.SyncProductStockService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.usecase.utils.MockData.assertIsIsoInstant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateProductNameUseCaseTest {

    @Mock
    ProductGateway productGateway;

    @Mock
    SyncProductStockService syncProductStockService;

    @InjectMocks
    UpdateProductNameUseCase useCase;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @Test
    void executeShouldUpdateNameAndSyncWhenProductExists() {
        // Arrange
        String productId = "P1";
        Product existing = MockData.productSaved(productId, "Old", 10);
        Product input = MockData.productInput("New", 10);
        Product saved = existing.toBuilder().name("New").build();

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(saved));
        given(syncProductStockService.updateProductName(productId, "New")).willReturn(Mono.empty());

        // Act
        Mono<Product> result = useCase.execute(input, productId);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(productGateway).saveProduct(productCaptor.capture());
        Product toPersist = productCaptor.getValue();
        assert "New".equals(toPersist.getName());
        assertIsIsoInstant(toPersist.getModificationDate());
    }

    @Test
    void executeShouldErrorWhenProductNotFound() {
        // Arrange
        String productId = "all";
        Product input = MockData.productInput("Whatever", 5);

        given(productGateway.getProductById(productId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_PRODUCT_FOUND)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSaveFails() {
        // Arrange
        String productId = "P2";
        Product existing = MockData.productSaved(productId, "Any", 3);
        Product input = MockData.productInput("NewName", 3);

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class)))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSyncFails() {
        // Arrange
        String productId = "P3";
        Product existing = MockData.productSaved(productId, "Old", 4);
        Product input = MockData.productInput("New", 4);
        Product saved = existing.toBuilder().name("New").build();

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(saved));
        given(syncProductStockService.updateProductName(productId, "New"))
                .willReturn(Mono.error(new RuntimeException("sync error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMessage("sync error")
                .verify();
    }
}
