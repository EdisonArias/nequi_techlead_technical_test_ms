package co.com.nequi.teachlead.technical.test.usecase.franchise.create;

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
class CreateFranchiseUseCaseTest {

    @Mock
    FranchiseGateway franchiseGateway;

    @InjectMocks
    CreateFranchiseUseCase useCase;

    @Captor
    ArgumentCaptor<Franchise> franchiseCaptor;

    @Test
    void executeShouldCreateFranchise() {
        // Arrange
        Franchise input = MockData.franchiseInput("Nike");
        Franchise saved = MockData.franchise("F1", "Nike");

        given(franchiseGateway.existsByName("Nike")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class))).willReturn(Mono.just(saved));

        // Act
        Mono<Franchise> result = useCase.execute(input);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(franchiseGateway).saveFranchise(franchiseCaptor.capture());
        Franchise toPersist = franchiseCaptor.getValue();
        assert "Nike".equals(toPersist.getName());
        assertIsIsoInstant(toPersist.getCreationDate());
        assertIsIsoInstant(toPersist.getModificationDate());
    }

    @Test
    void executeShouldErrorWhenNameAlreadyExists() {
        // Arrange
        Franchise input = MockData.franchiseInput("Adidas");
        given(franchiseGateway.existsByName("Adidas")).willReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(useCase.execute(input))
                .expectErrorMatches(ex -> ex instanceof BusinessException
                        && ((BusinessException) ex).getType() == BusinessType.FRANCHISE_EXISTS)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSaveFails() {
        // Arrange
        Franchise input = MockData.franchiseInput("Puma");
        given(franchiseGateway.existsByName("Puma")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class)))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void executeShouldTrimNameBeforeExistenceCheck() {
        // Arrange
        Franchise input = MockData.franchiseInput("  Reebok  ");
        Franchise saved = MockData.franchise("F9", "Reebok");

        given(franchiseGateway.existsByName("Reebok")).willReturn(Mono.just(false));
        given(franchiseGateway.saveFranchise(any(Franchise.class))).willReturn(Mono.just(saved));

        // Act
        Mono<Franchise> result = useCase.execute(input);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(franchiseGateway).saveFranchise(franchiseCaptor.capture());
    }

}
