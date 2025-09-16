package co.com.nequi.teachlead.technical.test.model.franchise.gateway;

import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import reactor.core.publisher.Mono;

public interface ProductTopBranchGateway {
    Mono<BranchTopProduct> getTopProductForBranch(String branchId);
}
