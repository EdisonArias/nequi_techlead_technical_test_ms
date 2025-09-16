package co.com.nequi.teachlead.technical.test.usecase.product.get;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.ProductTopBranchGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class GetTopProductByBranchUseCase {

    private final FranchiseGateway franchiseGateway;
    private final ProductTopBranchGateway topQueryGateway;

    public Flux<BranchTopProduct> execute(String franchiseId) {
        return checkExistsFranchise(franchiseId)
                .flatMapMany(this::extractBranches)
                .concatMap(this::topForBranch);
    }


    private Mono<Franchise> checkExistsFranchise(String franchiseId) {
        return franchiseGateway.getFranchiseById(franchiseId)
                .switchIfEmpty(Mono.error(BusinessType.NO_FRANCHISE_FOUND.build(franchiseId)));
    }

    private Flux<Branch> extractBranches(Franchise franchise) {
        List<Branch> refs = franchise.getBranches();
        return Flux.fromIterable(Objects.requireNonNullElse(refs, List.of()));
    }

    private Mono<BranchTopProduct> topForBranch(Branch ref) {
        return topQueryGateway.getTopProductForBranch(ref.getId())
                .map(tp -> tp.toBuilder()
                        .branchId(ref.getId())
                        .branchName(ref.getName())
                        .build());
    }
}
