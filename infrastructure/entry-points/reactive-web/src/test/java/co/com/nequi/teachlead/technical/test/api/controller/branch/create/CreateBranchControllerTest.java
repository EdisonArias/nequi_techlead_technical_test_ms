package co.com.nequi.teachlead.technical.test.api.controller.branch.create;

import co.com.nequi.teachlead.technical.test.api.controller.branch.create.mapper.BranchMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.branch.create.CreateBranchUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.FRANCHISE_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBranchControllerTest {

    @Mock
    ValidateRequest validateRequest;

    @Mock
    CreateBranchUseCase createBranchUseCase;

    @Mock
    BranchMapper branchMapper;

    @InjectMocks
    CreateBranchController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        String franchiseId = "68c8b10c3b28d0b513632b27";

        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Bogota")
                .build();

        Branch branchEntity = Branch.builder().name("Bogota").build();

        Franchise updated = Franchise.builder()
                .id(franchiseId)
                .name("Franchise X")
                .build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        given(branchMapper.toEntity(rq)).willReturn(branchEntity);
        given(createBranchUseCase.execute(franchiseId, branchEntity)).willReturn(Mono.just(updated));

        // Act
        Mono<ServerResponse> result = controller.execute(request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verify(branchMapper).toEntity(rq);
        verify(createBranchUseCase).execute(franchiseId, branchEntity);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        String franchiseId = "68c8b10c3b28d0b513632b27";
        ServerRequest request = mock(ServerRequest.class);

        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.empty());
        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        // Act
        Mono<ServerResponse> result = controller.execute(request);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verifyNoInteractions(branchMapper, createBranchUseCase);
    }

    @Test
    void execute_ShouldPropagateError_WhenUseCaseFails() {
        // Arrange
        String franchiseId = "does-not-exist";
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder().name("Cali").build();
        Branch branchEntity = Branch.builder().name("Cali").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));
        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        given(branchMapper.toEntity(rq)).willReturn(branchEntity);
        given(createBranchUseCase.execute(franchiseId, branchEntity))
                .willReturn(Mono.error(BusinessType.NO_FRANCHISE_FOUND.build(franchiseId)));

        // Act
        Mono<ServerResponse> result = controller.execute(request);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.NO_FRANCHISE_FOUND)
                .verify();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verify(branchMapper).toEntity(rq);
        verify(createBranchUseCase).execute(franchiseId, branchEntity);
    }
}
