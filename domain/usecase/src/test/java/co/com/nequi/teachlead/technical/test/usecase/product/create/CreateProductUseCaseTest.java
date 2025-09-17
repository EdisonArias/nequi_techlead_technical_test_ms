package co.com.nequi.teachlead.technical.test.usecase.product.create;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.branch.services.AddProductBranchService;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
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
class CreateProductUseCaseTest {

    @Mock
    BranchGateway branchGateway;

    @Mock
    ProductGateway productGateway;

    @Mock
    AddProductBranchService addProductBranchService;

    @InjectMocks
    CreateProductUseCase useCase;

    @Captor
    ArgumentCaptor<Product> productCaptor;

    @Test
    void executeShouldCreateProductAndAddInBranch() {
        // Arrange
        String branchId = "B1";
        Product input = MockData.productInput("Tenis", 25);
        Branch existingBranch = MockData.branch(branchId, "Bogota");
        Product savedProduct = input.toBuilder().id("P1").build();
        Branch updatedBranch = existingBranch.toBuilder().build();

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existingBranch));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(savedProduct));
        given(addProductBranchService.addProduct(branchId, savedProduct)).willReturn(Mono.just(updatedBranch));

        // Act
        Mono<Branch> result = useCase.execute(branchId, input);

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedBranch)
                .verifyComplete();

        verify(productGateway).saveProduct(productCaptor.capture());
        Product built = productCaptor.getValue();
        assert "Tenis".equals(built.getName());
        assert Integer.valueOf(25).equals(built.getStock());
        assertIsIsoInstant(built.getCreationDate());
        assertIsIsoInstant(built.getModificationDate());
    }

    @Test
    void executeShouldErrorWhenBranchNotFound() {
        // Arrange
        String branchId = "B404";
        Product input = MockData.productInput("Camiseta", 10);

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, input))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_BRANCH_FOUND)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSaveProductFails() {
        // Arrange
        String branchId = "B1";
        Product input = MockData.productInput("Gorra", 5);
        Branch existingBranch = MockData.branch(branchId, "Medellin");

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existingBranch));
        given(productGateway.saveProduct(any(Product.class)))
                .willReturn(Mono.error(new RuntimeException("DB error saving product")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, input))
                .expectErrorMessage("DB error saving product")
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenEmbedInBranchFails() {
        // Arrange
        String branchId = "B1";
        Product input = MockData.productInput("Pantalon", 12);
        Branch existingBranch = MockData.branch(branchId, "Cali");
        Product saved = input.toBuilder().id("P9").build();

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existingBranch));
        given(productGateway.saveProduct(any(Product.class))).willReturn(Mono.just(saved));
        given(addProductBranchService.addProduct(branchId, saved))
                .willReturn(Mono.error(new RuntimeException("Embed error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(branchId, input))
                .expectErrorMessage("Embed error")
                .verify();
    }
}
