package co.com.nequi.teachlead.technical.test.api.handler.product;

import co.com.nequi.teachlead.technical.test.api.controller.product.get.GetTopProductsInBranchController;
import co.com.nequi.teachlead.technical.test.api.controller.product.create.CreateProductController;
import co.com.nequi.teachlead.technical.test.api.controller.product.delete.DeleteProductController;
import co.com.nequi.teachlead.technical.test.api.controller.product.get.GetProductsController;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.name.UpdateProductNameController;
import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.UpdateProductStockController;
import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static co.com.nequi.teachlead.technical.test.api.shared.constants.OperationMessages.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductHandler {

    private final CreateProductController createProductController;
    private final UpdateProductStockController updateProductStockController;
    private final UpdateProductNameController updateProductNameController;
    private final DeleteProductController deleteProductController;
    private final GetProductsController getProductsController;
    private final GetTopProductsInBranchController getTopProductsInBranchController;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return handleRequest(request, createProductController::execute,
                REQUEST_CREATE_PRODUCT, RESPONSE_CREATE_PRODUCT);
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        return handleRequest(request, updateProductStockController::execute,
                REQUEST_UPDATE_PRODUCT_STOCK, RESPONSE_UPDATE_PRODUCT_STOCK);
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        return handleRequest(request, updateProductNameController::execute,
                REQUEST_UPDATE_PRODUCT_NAME, RESPONSE_UPDATE_PRODUCT_NAME);
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return handleRequest(request, deleteProductController::execute,
                REQUEST_DELETE_PRODUCT, RESPONSE_DELETE_PRODUCT);
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        return handleRequest(request, getProductsController::execute,
                REQUEST_GET_PRODUCTS, RESPONSE_GET_PRODUCTS);
    }

    public Mono<ServerResponse> getTopProductsInBranchForFranchise(ServerRequest request) {
        return handleRequest(request, getTopProductsInBranchController::execute,
                REQUEST_GET_TOP_PRODUCTS, RESPONSE_GET_TOP_PRODUCTS);
    }

    private Mono<ServerResponse> handleRequest(
            ServerRequest request, Function<ServerRequest, Mono<ServerResponse>> controller,
            String logRequest, String logResponse) {
        String messageId = request.headers().firstHeader(Headers.MESSAGE_ID.getName());
        return Mono.just(request)
                .flatMap(controller)
                .doFirst(() -> log.info(logRequest, request, messageId))
                .doOnSuccess(response -> log.info(logResponse, response, messageId));
    }

}
