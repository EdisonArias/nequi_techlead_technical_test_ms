package co.com.nequi.teachlead.technical.test.api.controller.branch.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.usecase.branch.get.GetBranchesUseCase;
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
class GetBranchesControllerTest {

    @Mock
    GetBranchesUseCase getBranchesUseCase;

    @InjectMocks
    GetBranchesController controller;

    @Test
    void executeShouldReturnOkCaseReturnsList() {
        // Arrange
        Branch b1 = Branch.builder().id("b1").name("Bogota").build();
        Branch b2 = Branch.builder().id("b2").name("Tunja").build();

        given(getBranchesUseCase.execute()).willReturn(Flux.just(b1, b2));

        ServerRequest request = mock(ServerRequest.class);

        // Act
        Mono<ServerResponse> result = controller.execute(request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(resp ->
                        resp.statusCode().is2xxSuccessful()
                                && resp instanceof EntityResponse
                                && ((EntityResponse<?>) resp).entity() instanceof Response)
                .verifyComplete();
    }

    @Test
    void execute_ShouldPropagateError_WhenUseCaseFails() {
        // Arrange
        RuntimeException boom = new RuntimeException("db failure");
        given(getBranchesUseCase.execute()).willReturn(Flux.error(boom));
        ServerRequest request = mock(ServerRequest.class);

        // Act & Assert
        StepVerifier.create(controller.execute(request))
                .expectErrorMatches(e -> e.getMessage().equals("db failure"))
                .verify();
    }
}
