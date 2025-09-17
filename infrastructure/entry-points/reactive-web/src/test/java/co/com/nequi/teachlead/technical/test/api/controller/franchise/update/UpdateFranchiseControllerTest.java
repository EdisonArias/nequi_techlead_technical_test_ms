package co.com.nequi.teachlead.technical.test.api.controller.franchise.update;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.mapper.FranchiseMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.franchise.update.UpdateFranchiseUseCase;
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
class UpdateFranchiseControllerTest {

    @Mock
    ValidateRequest validateRequest;

    @Mock
    UpdateFranchiseUseCase updateFranchiseUseCase;

    @Mock
    FranchiseMapper franchiseMapper;

    @InjectMocks
    UpdateFranchiseController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        String franchiseId = "f123";
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Nuevo Nombre")
                .build();

        Franchise mapped = Franchise.builder().name("Nuevo Nombre").build();
        Franchise updated = Franchise.builder().id(franchiseId).name("Nuevo Nombre").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        given(franchiseMapper.toEntity(rq)).willReturn(mapped);
        given(updateFranchiseUseCase.execute(mapped, franchiseId)).willReturn(Mono.just(updated));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verify(franchiseMapper).toEntity(rq);
        verify(updateFranchiseUseCase).execute(mapped, franchiseId);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        String franchiseId = "f123";
        ServerRequest request = mock(ServerRequest.class);

        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.empty());

        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verifyNoInteractions(franchiseMapper, updateFranchiseUseCase);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        String franchiseId = "f123";
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Nike")
                .build();

        Franchise mapped = Franchise.builder().name("Nike").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.pathVariable(FRANCHISE_ID.getName())).willReturn(franchiseId);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        doNothing().when(validateRequest).requireFranchiseId(franchiseId);

        given(franchiseMapper.toEntity(rq)).willReturn(mapped);
        given(updateFranchiseUseCase.execute(mapped, franchiseId))
                .willReturn(Mono.error(BusinessType.FRANCHISE_EXISTS.build("Nike")));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.FRANCHISE_EXISTS)
                .verify();

        verify(validateRequest).requireFranchiseId(franchiseId);
        verify(franchiseMapper).toEntity(rq);
        verify(updateFranchiseUseCase).execute(mapped, franchiseId);
    }
}
