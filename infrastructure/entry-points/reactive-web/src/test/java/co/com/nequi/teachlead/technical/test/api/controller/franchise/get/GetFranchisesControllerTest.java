package co.com.nequi.teachlead.technical.test.api.controller.franchise.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.usecase.franchise.get.GetFranchisesUseCase;
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
class GetFranchisesControllerTest {

    @Mock
    GetFranchisesUseCase getFranchisesUseCase;

    @InjectMocks
    GetFranchisesController controller;

    @Test
    void executeShouldReturnOkWithResponse() {
        // Arrange
        Franchise f1 = Franchise.builder().id("f1").name("Nike").build();
        Franchise f2 = Franchise.builder().id("f2").name("Adidas").build();

        given(getFranchisesUseCase.execute()).willReturn(Flux.just(f1, f2));

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
        RuntimeException boom = new RuntimeException("db failure");
        given(getFranchisesUseCase.execute()).willReturn(Flux.error(boom));

        ServerRequest request = mock(ServerRequest.class);

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(e -> e.getMessage().equals("db failure"))
                .verify();
    }
}
