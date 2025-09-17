package co.com.nequi.teachlead.technical.test.api.handler.product;

import co.com.nequi.teachlead.technical.test.api.controller.product.create.CreateProductController;
import co.com.nequi.teachlead.technical.test.api.controller.product.delete.DeleteProductController;
import co.com.nequi.teachlead.technical.test.api.controller.product.get.GetProductsController;
import co.com.nequi.teachlead.technical.test.api.controller.product.get.GetTopProductsInBranchController;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.name.UpdateProductNameController;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.UpdateProductStockController;
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
class ProductHandlerTest {

    @Mock private CreateProductController createProductController;
    @Mock private UpdateProductStockController updateProductStockController;
    @Mock private UpdateProductNameController updateProductNameController;
    @Mock private DeleteProductController deleteProductController;
    @Mock private GetProductsController getProductsController;
    @Mock private GetTopProductsInBranchController getTopProductsInBranchController;

    @InjectMocks
    private ProductHandler handler;

    private ServerRequest request() {
        return MockServerRequest.builder().build();
    }

    @Test
    void createProductShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("ok").block();
        assert ok != null;
        given(createProductController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.createProduct(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(createProductController).execute(any(ServerRequest.class));
        verifyNoInteractions(updateProductStockController, updateProductNameController,
                deleteProductController, getProductsController, getTopProductsInBranchController);
    }

    @Test
    void createProductShouldPropagateError() {
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("create fail");
        given(createProductController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.createProduct(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void updateProductStockShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("stock ok").block();
        assert ok != null;
        given(updateProductStockController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.updateProductStock(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(updateProductStockController).execute(any(ServerRequest.class));
        verifyNoInteractions(createProductController, updateProductNameController,
                deleteProductController, getProductsController, getTopProductsInBranchController);
    }

    @Test
    void updateProductStockShouldPropagateError() {
        ServerRequest req = request();
        IllegalStateException boom = new IllegalStateException("stock fail");
        given(updateProductStockController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.updateProductStock(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void updateProductNameShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("name ok").block();
        assert ok != null;
        given(updateProductNameController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.updateProductName(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(updateProductNameController).execute(any(ServerRequest.class));
        verifyNoInteractions(createProductController, updateProductStockController,
                deleteProductController, getProductsController, getTopProductsInBranchController);
    }

    @Test
    void updateProductNameShouldPropagateError() {
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("name fail");
        given(updateProductNameController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.updateProductName(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void deleteProductShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("deleted").block();
        assert ok != null;
        given(deleteProductController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.deleteProduct(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(deleteProductController).execute(any(ServerRequest.class));
        verifyNoInteractions(createProductController, updateProductStockController,
                updateProductNameController, getProductsController, getTopProductsInBranchController);
    }

    @Test
    void deleteProductShouldPropagateError() {
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("delete fail");
        given(deleteProductController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.deleteProduct(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void getAllProductsShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("list").block();
        assert ok != null;
        given(getProductsController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.getAllProducts(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(getProductsController).execute(any(ServerRequest.class));
        verifyNoInteractions(createProductController, updateProductStockController,
                updateProductNameController, deleteProductController, getTopProductsInBranchController);
    }

    @Test
    void getAllProductsShouldPropagateError() {
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("list fail");
        given(getProductsController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.getAllProducts(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }

    @Test
    void getTopProductsInBranchForFranchiseShouldDelegateToController() {
        ServerRequest req = request();
        ServerResponse ok = ServerResponse.ok().bodyValue("top").block();
        assert ok != null;
        given(getTopProductsInBranchController.execute(any(ServerRequest.class))).willReturn(Mono.just(ok));

        StepVerifier.create(handler.getTopProductsInBranchForFranchise(req))
                .expectNextMatches(resp -> resp.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(getTopProductsInBranchController).execute(any(ServerRequest.class));
        verifyNoInteractions(createProductController, updateProductStockController,
                updateProductNameController, deleteProductController, getProductsController);
    }

    @Test
    void getTopProductsInBranchForFranchise() {
        ServerRequest req = request();
        RuntimeException boom = new RuntimeException("top fail");
        given(getTopProductsInBranchController.execute(any(ServerRequest.class))).willReturn(Mono.error(boom));

        StepVerifier.create(handler.getTopProductsInBranchForFranchise(req))
                .expectErrorMatches(t -> t == boom)
                .verify();
    }
}
