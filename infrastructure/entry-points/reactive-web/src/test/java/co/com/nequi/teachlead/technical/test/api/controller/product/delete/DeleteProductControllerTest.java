package co.com.nequi.teachlead.technical.test.api.controller.product.delete;

import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.delete.DeleteProductUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.PRODUCT_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductControllerTest {

    @Mock ValidateRequest validateRequest;
    @Mock DeleteProductUseCase useCase;

    @InjectMocks
    DeleteProductController controller;

    @Test
    void executeShouldReturnOkWhenUseCaseCompletes() {
        // Arrange
        String productId = "p999";
        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(PRODUCT_ID.getName())).willReturn(productId);

        doNothing().when(validateRequest).requireFranchiseId(productId);
        given(useCase.execute(productId)).willReturn(Mono.empty());

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(validateRequest).requireFranchiseId(productId);
        verify(useCase).execute(productId);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        String productId = "p999";
        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(PRODUCT_ID.getName())).willReturn(productId);

        doNothing().when(validateRequest).requireFranchiseId(productId);
        given(useCase.execute(productId))
                .willReturn(Mono.error(BusinessType.NO_PRODUCT_FOUND.build(productId)));

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.NO_PRODUCT_FOUND)
                .verify();

        verify(validateRequest).requireFranchiseId(productId);
        verify(useCase).execute(productId);
    }
}
