package co.com.nequi.teachlead.technical.test.api.controller.branch.update;

import co.com.nequi.teachlead.technical.test.api.controller.branch.create.mapper.BranchMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.branch.update.UpdateBranchUseCase;
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
class UpdateBranchControllerTest {

    @Mock
    ValidateRequest validateRequest;

    @Mock
    UpdateBranchUseCase updateBranchUseCase;

    @Mock
    BranchMapper branchMapper;

    @InjectMocks
    UpdateBranchController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        String branchId = "68c8d7e6c628db8c8801802c";

        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Nueva Sucursal")
                .build();

        Branch mapped = Branch.builder().name("Nueva Sucursal").build();
        Branch updated = Branch.builder().id(branchId).name("Nueva Sucursal").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        // validator (no-op)
        doNothing().when(validateRequest).requireFranchiseId(branchId);

        given(branchMapper.toEntity(rq)).willReturn(mapped);
        given(updateBranchUseCase.execute(mapped, branchId)).willReturn(Mono.just(updated));

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
        verify(branchMapper).toEntity(rq);
        verify(updateBranchUseCase).execute(mapped, branchId);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        String branchId = "68c8d7e6c628db8c8801802c";
        ServerRequest request = mock(ServerRequest.class);

        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.empty());

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
        verifyNoInteractions(branchMapper, updateBranchUseCase);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        String branchId = "not-found";
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Cali Centro")
                .build();

        Branch mapped = Branch.builder().name("Cali Centro").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(BRANCH_ID.getName())).willReturn(branchId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(branchId);

        given(branchMapper.toEntity(rq)).willReturn(mapped);
        given(updateBranchUseCase.execute(mapped, branchId))
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
        verify(branchMapper).toEntity(rq);
        verify(updateBranchUseCase).execute(mapped, branchId);
    }
}
