package co.com.nequi.teachlead.technical.test.api.router.product;

import co.com.nequi.teachlead.technical.test.api.shared.constants.ApiPaths;
import co.com.nequi.teachlead.technical.test.api.handler.product.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {
    @Bean
    public RouterFunction<ServerResponse> routerProduct(ProductHandler handler) {
        return route(POST(ApiPaths.CREATE_PRODUCT), handler::createProduct)
                .andRoute(PUT(ApiPaths.UPDATE_PRODUCT_STOCK), handler::updateProductStock)
                .andRoute(PUT(ApiPaths.UPDATE_PRODUCT_NAME), handler::updateProductName)
                .andRoute(GET(ApiPaths.GET_ALL_PRODUCTS), handler::getAllProducts)
                .andRoute(DELETE(ApiPaths.DELETE_PRODUCT), handler::deleteProduct)
                .andRoute(GET(ApiPaths.TOP_PRODUCTS_BY_BRANCH), handler::getTopProductsInBranchForFranchise);
    }
}
