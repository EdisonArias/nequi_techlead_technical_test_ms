package co.com.nequi.teachlead.technical.test.usecase.franchise.get;

import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetFranchisesUseCaseTest {

    @Mock
    FranchiseGateway franchiseGateway;

    @InjectMocks
    GetFranchisesUseCase useCase;

    @Test
    void executeShouldReturnListOfFranchises() {
        // Arrange
        List<Franchise> franchises = List.of(
                MockData.franchise("F1", "Nike"),
                MockData.franchise("F2", "Adidas")
        );
        given(franchiseGateway.getAllFranchises()).willReturn(Flux.fromIterable(franchises));

        // Act
        Flux<Franchise> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectNextSequence(franchises)
                .verifyComplete();
    }

    @Test
    void executeShouldReturnEmptyWhenNoFranchises() {
        // Arrange
        given(franchiseGateway.getAllFranchises()).willReturn(Flux.empty());

        // Act
        Flux<Franchise> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void executeShouldPropagateErrorWhenGatewayFails() {
        // Arrange
        given(franchiseGateway.getAllFranchises())
                .willReturn(Flux.error(new RuntimeException("DB error")));

        // Act
        Flux<Franchise> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("DB error")
                .verify();
    }
}
