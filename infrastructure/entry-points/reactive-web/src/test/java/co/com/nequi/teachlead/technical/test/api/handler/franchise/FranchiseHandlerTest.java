package co.com.nequi.teachlead.technical.test.api.handler.franchise;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.CreateFranchiseController;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.get.GetFranchisesController;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.update.UpdateFranchiseController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseController createFranchiseController;
    @Mock
    private UpdateFranchiseController updateFranchiseController;
    @Mock
    private GetFranchisesController getFranchisesController;

    @InjectMocks
    private FranchiseHandler handler;

    private ServerRequest request() {
        return MockServerRequest.builder().build();
    }


    @Test
    void createFranchiseShouldDelegateToController() {
        // Arrange
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("ok").block();
        assert ok != null;
        given(createFranchiseController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.createFranchise(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(createFranchiseController).execute(captor.capture());
        verifyNoInteractions(updateFranchiseController, getFranchisesController);
    }

    @Test
    void createFranchiseShouldPropagateError() {
        // Arrange
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("controller error");
        given(createFranchiseController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.createFranchise(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }


    @Test
    void updateFranchiseShouldDelegateToController() {
        // Arrange
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("updated").block();
        assert ok != null;
        given(updateFranchiseController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.updateFranchise(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(updateFranchiseController).execute(captor.capture());
        verifyNoInteractions(createFranchiseController, getFranchisesController);
    }

    @Test
    void updateFranchiseShouldPropagateError() {
        // Arrange
        ServerRequest req = request();
        IllegalStateException boom = new IllegalStateException("fail");
        given(updateFranchiseController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.updateFranchise(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void getAllFranchisesShouldDelegateToController() {
        // Arrange
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("list").block();
        assert ok != null;
        given(getFranchisesController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.getAllFranchises(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(getFranchisesController).execute(captor.capture());
        verifyNoInteractions(createFranchiseController, updateFranchiseController);
    }

    @Test
    void getAllFranchisesShouldPropagateError() {
        // Arrange
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("oops");
        given(getFranchisesController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.getAllFranchises(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }
}
