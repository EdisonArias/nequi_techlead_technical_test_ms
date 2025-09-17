package co.com.nequi.teachlead.technical.test.usecase.product.get;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetProductsUseCaseTest {

    @Mock
    ProductGateway productGateway;

    @InjectMocks
    GetProductsUseCase useCase;

    @Test
    void executeShouldReturnListOfProducts() {
        // Arrange
        List<Product> products = List.of(
                MockData.productSaved("P1", "Tenis", 25),
                MockData.productSaved("P2", "Camiseta", 10)
        );
        given(productGateway.getAllProducts()).willReturn(Flux.fromIterable(products));

        // Act
        Flux<Product> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectNextSequence(products)
                .verifyComplete();
    }

    @Test
    void executeShouldReturnEmptyWhenNoProducts() {
        // Arrange
        given(productGateway.getAllProducts()).willReturn(Flux.empty());

        // Act
        Flux<Product> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void executeShouldPropagateErrorWhenGatewayFails() {
        // Arrange
        given(productGateway.getAllProducts())
                .willReturn(Flux.error(new RuntimeException("DB error")));

        // Act
        Flux<Product> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("DB error")
                .verify();
    }
}
