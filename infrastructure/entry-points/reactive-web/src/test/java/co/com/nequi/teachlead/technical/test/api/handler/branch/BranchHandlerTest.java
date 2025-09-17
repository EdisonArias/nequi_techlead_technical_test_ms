package co.com.nequi.teachlead.technical.test.api.handler.branch;

import co.com.nequi.teachlead.technical.test.api.controller.branch.create.CreateBranchController;
import co.com.nequi.teachlead.technical.test.api.controller.branch.get.GetBranchesController;
import co.com.nequi.teachlead.technical.test.api.controller.branch.update.UpdateBranchController;
import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private CreateBranchController createBranchController;
    @Mock
    private UpdateBranchController updateBranchController;
    @Mock
    private GetBranchesController getBranchesController;

    @InjectMocks
    private BranchHandler handler;

    private static final String MESSAGE_ID = "mid-123";

    private ServerRequest requestWithMessageId() {
        return MockServerRequest.builder()
                .header(Headers.MESSAGE_ID.getName(), MESSAGE_ID)
                .build();
    }


    @Test
    void createBranchShouldDelegateToController() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        ServerResponse ok = ServerResponse.ok().bodyValue("ok").block();
        assert ok != null;
        given(createBranchController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.createBranch(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(createBranchController).execute(captor.capture());
        verifyNoInteractions(updateBranchController, getBranchesController);
    }

    @Test
    void createBranchShouldPropagateError() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        RuntimeException boom = new RuntimeException("controller error");
        given(createBranchController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.createBranch(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }


    @Test
    void updateBranchShouldDelegateToController() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        ServerResponse ok = ServerResponse.ok().bodyValue("updated").block();
        assert ok != null;
        given(updateBranchController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.updateBranch(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(updateBranchController).execute(captor.capture());
        verifyNoInteractions(createBranchController, getBranchesController);
    }

    @Test
    void updateBranchShouldPropagateError() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        IllegalStateException boom = new IllegalStateException("fail");
        given(updateBranchController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.updateBranch(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }


    @Test
    void geAllBranchesShouldDelegateToController() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        ServerResponse ok = ServerResponse.ok().bodyValue("list").block();
        assert ok != null;
        given(getBranchesController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        // Act
        Mono<ServerResponse> out = handler.geAllBranches(req);

        // Assert
        StepVerifier.create(out)
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        ArgumentCaptor<ServerRequest> captor = ArgumentCaptor.forClass(ServerRequest.class);
        verify(getBranchesController).execute(captor.capture());
        verifyNoInteractions(createBranchController, updateBranchController);
    }

    @Test
    void geAllBranchesShouldPropagateError() {
        // Arrange
        ServerRequest req = requestWithMessageId();
        RuntimeException boom = new RuntimeException("oops");
        given(getBranchesController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        // Act & Assert
        StepVerifier.create(handler.geAllBranches(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }
}
