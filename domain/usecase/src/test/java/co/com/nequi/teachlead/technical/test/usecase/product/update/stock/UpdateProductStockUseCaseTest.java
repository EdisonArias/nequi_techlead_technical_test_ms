package co.com.nequi.teachlead.technical.test.usecase.product.update.stock;

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
class UpdateProductStockUseCaseTest {

    @Mock
    ProductGateway productGateway;

    @Mock
    SyncProductStockService syncProductStockService;

    @InjectMocks
    UpdateProductStockUseCase useCase;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @Test
    void executeShouldUpdateStockAndSyncWhenProductExists() {
        // Arrange
        String productId = "P1";
        int newStock = 42;

        Product existing = MockData.productSaved(productId, "Zapatos", 10);
        Product input = MockData.productInput("Zapatos", newStock);
        Product saved = existing.toBuilder().stock(newStock).build();

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(saved));
        given(syncProductStockService.updateProductStock(productId, newStock)).willReturn(Mono.empty());

        // Act
        Mono<Product> result = useCase.execute(input, productId);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(productGateway).saveProduct(productCaptor.capture());
        Product toPersist = productCaptor.getValue();
        assert Integer.valueOf(newStock).equals(toPersist.getStock());
        assertIsIsoInstant(toPersist.getModificationDate());
    }

    @Test
    void execute_ShouldError_WhenProductNotFound() {
        // Arrange
        String productId = "NOPE";
        Product input = MockData.productInput("Camiseta", 7);

        given(productGateway.getProductById(productId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_PRODUCT_FOUND)
                .verify();
    }

    @Test
    void execute_ShouldPropagateError_WhenSaveFails() {
        // Arrange
        String productId = "P2";
        Product existing = MockData.productSaved(productId, "Gorra", 3);
        Product input = MockData.productInput("Gorra", 99);

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class)))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void execute_ShouldPropagateError_WhenSyncFails() {
        // Arrange
        String productId = "P3";
        int newStock = 13;
        Product existing = MockData.productSaved(productId, "Pantalon", 2);
        Product input = MockData.productInput("Pantalon", newStock);
        Product saved = existing.toBuilder().stock(newStock).build();

        given(productGateway.getProductById(productId)).willReturn(Mono.just(existing));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(saved));
        given(syncProductStockService.updateProductStock(productId, newStock))
                .willReturn(Mono.error(new RuntimeException("sync error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, productId))
                .expectErrorMessage("sync error")
                .verify();
    }
}
