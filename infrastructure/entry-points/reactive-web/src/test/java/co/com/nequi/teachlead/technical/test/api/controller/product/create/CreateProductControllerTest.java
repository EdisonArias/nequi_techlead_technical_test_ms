package co.com.nequi.teachlead.technical.test.api.controller.product.create;

import co.com.nequi.teachlead.technical.test.api.controller.product.create.mapper.ProductMapper;
import co.com.nequi.teachlead.technical.test.api.controller.product.create.request.CreateProduct;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.product.create.CreateProductUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.BRANCH_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductControllerTest {

    @Mock
    ValidateRequest validateRequest;

    @Mock
    CreateProductUseCase createProductUseCase;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    CreateProductController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        String branchId = "b123";
        CreateProduct rq = CreateProduct.builder()
                .name("Gaseosa")
                .stock(10)
                .build();

        Product mapped = Product.builder().name("Gaseosa").stock(10).build();
        Branch updatedBranch = Branch.builder().id(branchId).name("Sucursal Norte").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateProduct.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(branchId);
        doNothing().when(validateRequest).validate(rq);

        given(productMapper.toEntity(rq)).willReturn(mapped);
        given(createProductUseCase.execute(branchId, mapped)).willReturn(Mono.just(updatedBranch));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();

        verify(validateRequest).requireFranchiseId(branchId);
        verify(validateRequest).validate(rq);
        verify(productMapper).toEntity(rq);
        verify(createProductUseCase).execute(branchId, mapped);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        String branchId = "b123";
        ServerRequest request = mock(ServerRequest.class);

        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateProduct.class)).willReturn(Mono.empty());

        doNothing().when(validateRequest).requireFranchiseId(branchId);

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verify(validateRequest).requireFranchiseId(branchId);
        verifyNoInteractions(productMapper, createProductUseCase);
    }

    @Test
    void executeShouldPropagateErrorWhenValidationFails() {
        // Arrange
        String branchId = "b123";
        CreateProduct rq = CreateProduct.builder()
                .name("Gaseosa")
                .stock(null)
                .build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateProduct.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(branchId);
        doThrow(BusinessType.BAD_REQUEST.build()).when(validateRequest).validate(rq);

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verify(validateRequest).requireFranchiseId(branchId);
        verify(validateRequest).validate(rq);
        verifyNoInteractions(productMapper, createProductUseCase);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        String branchId = "b123";
        CreateProduct rq = CreateProduct.builder().name("Gaseosa").stock(5).build();
        Product mapped = Product.builder().name("Gaseosa").stock(5).build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateProduct.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(branchId);
        doNothing().when(validateRequest).validate(rq);

        given(productMapper.toEntity(rq)).willReturn(mapped);
        given(createProductUseCase.execute(branchId, mapped))
                .willReturn(Mono.error(BusinessType.NO_BRANCH_FOUND.build(branchId)));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.NO_BRANCH_FOUND)
                .verify();

        verify(validateRequest).requireFranchiseId(branchId);
        verify(validateRequest).validate(rq);
        verify(productMapper).toEntity(rq);
        verify(createProductUseCase).execute(branchId, mapped);
    }
}
