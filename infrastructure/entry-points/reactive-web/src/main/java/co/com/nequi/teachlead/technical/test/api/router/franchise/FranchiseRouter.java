package co.com.nequi.teachlead.technical.test.api.router.franchise;

import co.com.nequi.teachlead.technical.test.api.shared.constants.ApiPaths;
import co.com.nequi.teachlead.technical.test.api.handler.franchise.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {
    @Bean
    public RouterFunction<ServerResponse> routerFranchise(FranchiseHandler handler) {
        return route(POST(ApiPaths.FRANCHISES), handler::createFranchise)
                .andRoute(PUT(ApiPaths.UPDATE_FRANCHISE), handler::updateFranchise)
                .andRoute(GET(ApiPaths.FRANCHISES), handler::getAllFranchises);
    }
}
