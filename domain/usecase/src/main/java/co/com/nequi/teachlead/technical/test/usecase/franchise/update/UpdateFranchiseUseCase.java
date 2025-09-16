package co.com.nequi.teachlead.technical.test.usecase.franchise.update;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateFranchiseUseCase {

    private static final Logger log = Logger.getLogger(UpdateFranchiseUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_FRANCHISE_UPDATED = "Update Franchise: ";

    private final FranchiseGateway repository;

    public Mono<Franchise> execute(Franchise newFranchise, String franchiseId) {
        return this.repository.getFranchiseById(franchiseId)
                .flatMap(franchiseFound ->
                        checkFranchiseNameExists(franchiseFound,newFranchise.getName()))
                .map(franchiseValidated -> buildUpdateFranchise(franchiseValidated,newFranchise.getName()))
                .flatMap(repository::saveFranchise)
                .doOnNext(saved -> log.info(MESSAGE_FRANCHISE_UPDATED + saved))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.NO_FRANCHISE_FOUND.build(franchiseId))));
    }

    private Mono<Franchise> checkFranchiseNameExists(Franchise franchise , String newFranchiseName) {
        return repository.existsByName(newFranchiseName.trim())
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.FRANCHISE_EXISTS.build(newFranchiseName))))
                .thenReturn(franchise);
    }

    private Franchise buildUpdateFranchise(Franchise franchiseNew , String newName) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return franchiseNew.toBuilder()
                .name(newName)
                .modificationDate(now.format(formatter))
                .build();
    }
}
