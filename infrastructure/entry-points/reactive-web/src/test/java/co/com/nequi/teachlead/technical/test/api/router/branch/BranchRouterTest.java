package co.com.nequi.teachlead.technical.test.api.router.branch;

import co.com.nequi.teachlead.technical.test.api.handler.branch.BranchHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class BranchRouterTest {

    private WebTestClient client;
    private BranchHandler handler;

    @BeforeEach
    void setup() {
        handler = mock(BranchHandler.class);
        RouterFunction<ServerResponse> routes = new BranchRouter().routerBranch(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void createBranch_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("created").block();
        assert ok != null;
        when(handler.createBranch(ArgumentMatchers.any(ServerRequest.class)))
                .thenReturn(Mono.just(ok));

        client.post()
                .uri("/api/v1/franchises/abc123/branches")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("created");

        verify(handler).createBranch(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void updateBranch_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("updated").block();
        assert ok != null;
        when(handler.updateBranch(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.put()
                .uri("/api/v1/franchises/branches/branch-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("updated");

        verify(handler).updateBranch(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void getAllBranches_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("list").block();
        assert ok != null;
        when(handler.geAllBranches(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.get()
                .uri("/api/v1/franchises/branches")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("list");

        verify(handler).geAllBranches(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void createBranch_whenHandlerErrors_ShouldReturn5xx() {
        when(handler.createBranch(any(ServerRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        client.post()
                .uri("/api/v1/franchises/abc/branches")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(handler).createBranch(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }
}
