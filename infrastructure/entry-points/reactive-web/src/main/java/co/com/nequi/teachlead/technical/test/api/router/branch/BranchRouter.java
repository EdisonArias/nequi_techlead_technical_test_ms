package co.com.nequi.teachlead.technical.test.api.router.branch;

import co.com.nequi.teachlead.technical.test.api.shared.constants.ApiPaths;
import co.com.nequi.teachlead.technical.test.api.handler.branch.BranchHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BranchRouter {
    @Bean
    public RouterFunction<ServerResponse> routerBranch(BranchHandler handler) {
        return route(POST(ApiPaths.BRANCHES_BY_FRANCHISE_ID), handler::createBranch)
                .andRoute(PUT(ApiPaths.UPDATE_BRANCH), handler::updateBranch)
                .andRoute(GET(ApiPaths.GET_ALL_BRANCHES), handler::geAllBranches);
    }
}
