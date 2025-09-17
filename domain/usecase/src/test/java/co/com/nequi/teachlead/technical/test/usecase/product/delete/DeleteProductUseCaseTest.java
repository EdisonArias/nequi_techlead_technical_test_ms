package co.com.nequi.teachlead.technical.test.usecase.product.delete;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.model.product.services.SyncDeleteProductService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    ProductGateway productGateway;

    @Mock
    SyncDeleteProductService syncDeleteProductService;

    @InjectMocks
    DeleteProductUseCase useCase;

    @Test
    void executeShouldRemoveEmbeddedAndDelete() {
        // Arrange
        String productId = "P1";
        Product product = MockData.productSaved(productId, "Zapatos", 10);

        given(productGateway.getProductById(productId)).willReturn(Mono.just(product));
        given(syncDeleteProductService.removeCategoryFromBranch(productId)).willReturn(Mono.empty());
        given(productGateway.deleteProduct(productId)).willReturn(Mono.empty());

        // Act
        Mono<Void> result = useCase.execute(productId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(syncDeleteProductService).removeCategoryFromBranch(productId);
        verify(productGateway).deleteProduct(productId);
    }

    @Test
    void executeShouldErrorWhenProductNotFound() {
        // Arrange
        String productId = "NOPE";
        given(productGateway.getProductById(productId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(productId))
                .expectErrorMatches(ex -> ex instanceof BusinessException
                        && ((BusinessException) ex).getType() == BusinessType.NO_PRODUCT_FOUND)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSyncRemovalFails() {
        // Arrange
        String productId = "P2";
        Product product = MockData.productSaved(productId, "Camiseta", 7);

        given(productGateway.getProductById(productId)).willReturn(Mono.just(product));
        given(syncDeleteProductService.removeCategoryFromBranch(productId))
                .willReturn(Mono.error(new RuntimeException("sync remove failed")));

        // Act & Assert
        StepVerifier.create(useCase.execute(productId))
                .expectErrorMessage("p2")
                .verify();
    }
}
