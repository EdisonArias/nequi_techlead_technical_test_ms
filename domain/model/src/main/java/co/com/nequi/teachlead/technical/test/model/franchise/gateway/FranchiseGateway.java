package co.com.nequi.teachlead.technical.test.model.franchise.gateway;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseGateway {
    Flux<Franchise> getAllFranchises();
    Mono<Franchise> getFranchiseById(String brandId);
    Mono<Franchise> saveFranchise(Franchise franchise);
    Mono<Boolean> existsByName(String franchiseName);
}
