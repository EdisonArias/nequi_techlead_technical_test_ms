package co.com.nequi.teachlead.technical.test.api.router.franchise;

import co.com.nequi.teachlead.technical.test.api.handler.franchise.FranchiseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class FranchiseRouterTest {

    private WebTestClient client;
    private FranchiseHandler handler;

    @BeforeEach
    void setup() {
        handler = mock(FranchiseHandler.class);
        RouterFunction<ServerResponse> routes = new FranchiseRouter().routerFranchise(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void createFranchise_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("created").block();
        assert ok != null;
        when(handler.createFranchise(ArgumentMatchers.any(ServerRequest.class)))
                .thenReturn(Mono.just(ok));

        client.post()
                .uri("/api/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("created");

        verify(handler).createFranchise(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void updateFranchise_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("updated").block();
        assert ok != null;
        when(handler.updateFranchise(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.put()
                .uri("/api/v1/franchises/fr-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("updated");

        verify(handler).updateFranchise(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void getAllFranchises_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("list").block();
        assert ok != null;
        when(handler.getAllFranchises(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.get()
                .uri("/api/v1/franchises")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("list");

        verify(handler).getAllFranchises(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void updateFranchise_whenHandlerErrors_ShouldReturn5xx() {
        when(handler.updateFranchise(any(ServerRequest.class)))
                .thenReturn(Mono.error(new IllegalStateException("fail")));

        client.put()
                .uri("/api/v1/franchises/xyz")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(handler).updateFranchise(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }
}
