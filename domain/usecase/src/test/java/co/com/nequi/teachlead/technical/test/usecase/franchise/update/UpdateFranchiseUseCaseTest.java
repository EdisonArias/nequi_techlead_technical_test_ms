package co.com.nequi.teachlead.technical.test.usecase.franchise.update;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.usecase.utils.MockData.assertIsIsoInstant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseUseCaseTest {

    @Mock
    FranchiseGateway franchiseGateway;

    @InjectMocks
    UpdateFranchiseUseCase useCase;

    @Captor
    ArgumentCaptor<Franchise> franchiseCaptor;

    @Test
    void executeShouldUpdateFranchiseWhenFound() {
        // Arrange
        String franchiseId = "F1";
        Franchise existing = MockData.franchise(franchiseId, "Old Name");
        Franchise input = MockData.franchiseInput("New Name");
        Franchise saved = existing.toBuilder()
                .name("New Name")
                .modificationDate("2025-09-16T00:00:00Z")
                .build();

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(franchiseGateway.existsByName("New Name")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class))).willReturn(Mono.just(saved));

        // Act
        Mono<Franchise> result = useCase.execute(input, franchiseId);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(franchiseGateway).saveFranchise(franchiseCaptor.capture());
        Franchise toPersist = franchiseCaptor.getValue();
        assert "New Name".equals(toPersist.getName());
        assertIsIsoInstant(toPersist.getModificationDate());
    }

    @Test
    void executeShouldErrorWhenFranchiseNotFound() {
        // Arrange
        String franchiseId = "F404";
        Franchise input = MockData.franchiseInput("Whatever");

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(input, franchiseId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_FRANCHISE_FOUND)
                .verify();
    }

    @Test
    void executeShouldErrorWhenNewNameAlreadyExists() {
        // Arrange
        String franchiseId = "F1";
        Franchise existing = MockData.franchise(franchiseId, "Old Name");
        Franchise input = MockData.franchiseInput("Nike");

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(franchiseGateway.existsByName("Nike")).willReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, franchiseId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.FRANCHISE_EXISTS)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSaveFails() {
        // Arrange
        String franchiseId = "F1";
        Franchise existing = MockData.franchise(franchiseId, "Old Name");
        Franchise input = MockData.franchiseInput("Updated");

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(franchiseGateway.existsByName("Updated")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class)))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, franchiseId))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void executeShouldTrimNewNameBeforeExistenceCheck() {
        // Arrange
        String franchiseId = "F2";
        Franchise existing = MockData.franchise(franchiseId, "Something");
        Franchise input = MockData.franchiseInput("   Reebok   ");
        Franchise saved = existing.toBuilder().name("Reebok").build();

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(franchiseGateway.existsByName("Reebok")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class))).willReturn(Mono.just(saved));

        // Act
        Mono<Franchise> result = useCase.execute(input, franchiseId);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(franchiseGateway).saveFranchise(franchiseCaptor.capture());
    }
}
