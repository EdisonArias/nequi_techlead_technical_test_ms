package co.com.nequi.teachlead.technical.test.usecase.franchise.create;

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
public class CreateFranchiseUseCase {

    private static final Logger log = Logger.getLogger(CreateFranchiseUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_FRANCHISE_CREATED = "Saved Franchise: ";

    private final FranchiseGateway repository;

    public Mono<Franchise> execute(Franchise payload) {
        return Mono.just(payload)
                .flatMap(this::checkFranchiseExists)
                .map(this::buildFranchise)
                .flatMap(repository::saveFranchise)
                .doOnNext(saved -> log.info(MESSAGE_FRANCHISE_CREATED + saved));
    }

    private Mono<Franchise> checkFranchiseExists(Franchise franchise) {
        return repository.existsByName(franchise.getName().trim())
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.FRANCHISE_EXISTS.build(franchise.getName()))))
                .thenReturn(franchise);
    }

    private Franchise buildFranchise(Franchise info) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return Franchise.builder()
                .name(info.getName())
                .creationDate(now.format(formatter))
                .modificationDate(now.format(formatter))
                .build();
    }
}
