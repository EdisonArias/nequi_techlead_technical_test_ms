package co.com.nequi.teachlead.technical.test.api.controller.franchise.create;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.mapper.FranchiseMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.franchise.create.CreateFranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseControllerTest {

    @Mock
    CreateFranchiseUseCase createFranchiseUseCase;

    @Mock
    FranchiseMapper franchiseMapper;

    @InjectMocks
    CreateFranchiseController controller;

    @Test
    void executeShouldReturnOkWhenPayloadValid() {
        // Arrange
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Nike")
                .build();

        Franchise mapped = Franchise.builder().name("Nike").build();
        Franchise created = Franchise.builder().id("f1").name("Nike").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        given(franchiseMapper.toEntity(rq)).willReturn(mapped);
        given(createFranchiseUseCase.execute(mapped)).willReturn(Mono.just(created));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();

        verify(franchiseMapper).toEntity(rq);
        verify(createFranchiseUseCase).execute(mapped);
    }

    @Test
    void executeShouldReturnBadRequestWhenBodyEmpty() {
        // Arrange
        ServerRequest request = mock(ServerRequest.class);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.empty());

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.BAD_REQUEST)
                .verify();

        verifyNoInteractions(franchiseMapper, createFranchiseUseCase);
    }

    @Test
    void executeShouldPropagateErrorWhenUseCaseFails() {
        // Arrange
        CreateAndUpdateFranchise rq = CreateAndUpdateFranchise.builder()
                .name("Nike")
                .build();

        Franchise mapped = Franchise.builder().name("Nike").build();

        ServerRequest request = mock(ServerRequest.class);
        given(request.bodyToMono(CreateAndUpdateFranchise.class)).willReturn(Mono.just(rq));

        given(franchiseMapper.toEntity(rq)).willReturn(mapped);
        given(createFranchiseUseCase.execute(mapped))
                .willReturn(Mono.error(BusinessType.FRANCHISE_EXISTS.build("Nike")));

        // Act
        Mono<ServerResponse> out = controller.execute(request);

        // Assert
        StepVerifier.create(out)
                .expectErrorMatches(err ->
                        err instanceof BusinessException
                                && ((BusinessException) err).getType() == BusinessType.FRANCHISE_EXISTS)
                .verify();

        verify(franchiseMapper).toEntity(rq);
        verify(createFranchiseUseCase).execute(mapped);
    }
}
