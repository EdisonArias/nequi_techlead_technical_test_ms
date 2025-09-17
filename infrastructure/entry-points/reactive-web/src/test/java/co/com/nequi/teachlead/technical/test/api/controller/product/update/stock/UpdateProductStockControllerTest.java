package co.com.nequi.teachlead.technical.test.api.controller.product.update.stock;

import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.mapper.ProductStockMapper;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.request.UpdateProductStock;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.update.stock.UpdateProductStockUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.PRODUCT_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockControllerTest {

    @Mock ValidateRequest validateRequest;
    @Mock UpdateProductStockUseCase useCase;
    @Mock
    ProductStockMapper productMapper;

    @InjectMocks
    UpdateProductStockController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        String productId = "p456";
        UpdateProductStock rq = UpdateProductStock.builder().stock(25).build();
        Product mapped = Product.builder().stock(25).build();
        Product updated = Product.builder().id(productId).stock(25).build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(PRODUCT_ID.getName())).willReturn(productId);
        given(request.bodyToMono(UpdateProductStock.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(productId);
        doNothing().when(validateRequest).validate(rq);

        given(productMapper.toEntity(rq)).willReturn(mapped);
        given(useCase.execute(mapped, productId)).willReturn(Mono.just(updated));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();

        verify(validateRequest).requireFranchiseId(productId);
        verify(validateRequest).validate(rq);
        verify(productMapper).toEntity(rq);
        verify(useCase).execute(mapped, productId);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        String productId = "p456";
        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(PRODUCT_ID.getName())).willReturn(productId);
        given(request.bodyToMono(UpdateProductStock.class)).willReturn(Mono.empty());

        doNothing().when(validateRequest).requireFranchiseId(productId);

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verify(validateRequest).requireFranchiseId(productId);
        verifyNoInteractions(productMapper, useCase);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        String productId = "p456";
        UpdateProductStock rq = UpdateProductStock.builder().stock(99).build();
        Product mapped = Product.builder().stock(99).build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(PRODUCT_ID.getName())).willReturn(productId);
        given(request.bodyToMono(UpdateProductStock.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(productId);
        doNothing().when(validateRequest).validate(rq);

        given(productMapper.toEntity(rq)).willReturn(mapped);
        given(useCase.execute(mapped, productId))
                .willReturn(Mono.error(BusinessType.NO_PRODUCT_FOUND.build(productId)));

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.NO_PRODUCT_FOUND)
                .verify();
    }
}
