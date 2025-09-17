package co.com.nequi.teachlead.technical.test.api.router.product;

import co.com.nequi.teachlead.technical.test.api.handler.product.ProductHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class ProductRouterTest {

    private WebTestClient client;
    private ProductHandler handler;

    @BeforeEach
    void setup() {
        handler = mock(ProductHandler.class);
        RouterFunction<ServerResponse> routes = new ProductRouter().routerProduct(handler);
        client = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void createProduct_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("created").block();
        assert ok != null;
        when(handler.createProduct(ArgumentMatchers.any(ServerRequest.class)))
                .thenReturn(Mono.just(ok));

        client.post()
                .uri("/api/v1/franchises/branches/br-001/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("created");

        verify(handler).createProduct(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void updateProductStock_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("stock-updated").block();
        assert ok != null;
        when(handler.updateProductStock(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.put()
                .uri("/api/v1/franchises/branches/products/p-001/stock")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("stock-updated");

        verify(handler).updateProductStock(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void updateProductName_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("name-updated").block();
        assert ok != null;
        when(handler.updateProductName(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.put()
                .uri("/api/v1/franchises/branches/products/p-001/name")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("name-updated");

        verify(handler).updateProductName(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void getAllProducts_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("all").block();
        assert ok != null;
        when(handler.getAllProducts(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.get()
                .uri("/api/v1/franchises/branches/products")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("all");

        verify(handler).getAllProducts(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void deleteProduct_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("deleted").block();
        assert ok != null;
        when(handler.deleteProduct(any(ServerRequest.class))).thenReturn(Mono.just(ok));

        client.delete()
                .uri("/api/v1/franchises/branches/products/p-001")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("deleted");

        verify(handler).deleteProduct(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void getTopProductsInBranchForFranchise_route_ShouldCallHandler() {
        ServerResponse ok = ServerResponse.ok().bodyValue("tops").block();
        assert ok != null;
        when(handler.getTopProductsInBranchForFranchise(any(ServerRequest.class)))
                .thenReturn(Mono.just(ok));

        client.get()
                .uri("/api/v1/franchises/fr-123/branches/topProducts")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("tops");

        verify(handler).getTopProductsInBranchForFranchise(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void createProduct_whenHandlerErrors_ShouldReturn5xx() {
        when(handler.createProduct(any(ServerRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        client.post()
                .uri("/api/v1/franchises/branches/br-001/products")
                .exchange()
                .expectStatus().is5xxServerError();

        verify(handler).createProduct(any(ServerRequest.class));
        verifyNoMoreInteractions(handler);
    }
}
