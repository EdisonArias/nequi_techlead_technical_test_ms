package co.com.nequi.teachlead.technical.test.api.controller.product.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.usecase.product.get.GetProductsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class GetProductsControllerTest {

    @Mock
    GetProductsUseCase getProductsUseCase;

    @InjectMocks
    GetProductsController controller;

    @Test
    void executeShouldReturnOkWithResponse() {
        // Arrange
        Product p1 = Product.builder().id("p1").name("Zapatos").stock(5).build();
        Product p2 = Product.builder().id("p2").name("Camisa").stock(7).build();

        given(getProductsUseCase.execute()).willReturn(Flux.just(p1, p2));
        ServerRequest request = mock(ServerRequest.class);

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        given(getProductsUseCase.execute()).willReturn(Flux.error(new RuntimeException("boom")));
        ServerRequest request = mock(ServerRequest.class);

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(e -> e.getMessage().equals("boom"))
                .verify();
    }
}
