package co.com.nequi.teachlead.technical.test.usecase.franchise.get;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;


@RequiredArgsConstructor
public class GetFranchisesUseCase {

    private final FranchiseGateway repository;

    public Flux<Franchise> execute() {
        return repository.getAllFranchises();
    }
}
