package co.com.nequi.teachlead.technical.test.api.shared.util;

import co.com.nequi.teachlead.technical.test.model.shared.exception.AppException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ValidateRequestTest {

    @Mock
    Validator validator;

    @InjectMocks
    ValidateRequest validateRequest;

    @Test
    void requireFranchiseIdShouldPass() {
        // Arrange
        String value = "franchise-123";

        // Act & Assert
        assertDoesNotThrow(() -> validateRequest.requireFranchiseId(value));
    }

    @Test
    void requireFranchiseIdShouldThrowWhenNull() {
        // Arrange
        String value = null;

        // Act
        AppException ex = assertThrows(AppException.class,
                () -> validateRequest.requireFranchiseId(value));

        // Assert
        assertEquals(BusinessType.NO_MESSAGE_ID, ex.getType());
    }

    @Test
    void requireFranchiseIdShouldThrowWhenBlank() {
        // Arrange
        String value = "  ";

        // Act
        AppException ex = assertThrows(AppException.class,
                () -> validateRequest.requireFranchiseId(value));

        // Assert
        assertEquals(BusinessType.NO_MESSAGE_ID, ex.getType());
    }

    @Test
    void requireMessageIdShouldPassWhenValueIsPresent() {
        // Arrange
        String messageId = "mid-123";

        // Act & Assert
        assertDoesNotThrow(() -> validateRequest.requireMessageId(messageId));
    }

    @Test
    void requireMessageIdShouldThrowWhenNull() {
        // Arrange
        String messageId = null;

        // Act
        AppException ex = assertThrows(AppException.class,
                () -> validateRequest.requireMessageId(messageId));

        // Assert
        assertEquals(BusinessType.NO_MESSAGE_ID, ex.getType());
    }

    @Test
    void requireMessageIdShouldThrowWhenBlank() {
        // Arrange
        String messageId = "  ";

        // Act
        AppException ex = assertThrows(AppException.class,
                () -> validateRequest.requireMessageId(messageId));

        // Assert
        assertEquals(BusinessType.NO_MESSAGE_ID, ex.getType());
    }

    @Test
    void validateShouldPassWhenNoViolations() {
        // Arrange
        Object anyDto = new Object();
        given(validator.validate(any())).willReturn(Collections.emptySet());

        // Act & Assert
        assertDoesNotThrow(() -> validateRequest.validate(anyDto));
    }

    @SuppressWarnings("unchecked")
    @Test
    void validateShouldThrowWithAggregatedMessages() {
        // Arrange
        Object dto = new Object();

        ConstraintViolation<Object> v1 = (ConstraintViolation<Object>) mock(ConstraintViolation.class);
        ConstraintViolation<Object> v2 = (ConstraintViolation<Object>) mock(ConstraintViolation.class);
        Path p1 = mock(Path.class);
        Path p2 = mock(Path.class);

        given(p1.toString()).willReturn("name");
        given(p2.toString()).willReturn("stock");
        given(v1.getPropertyPath()).willReturn(p1);
        given(v2.getPropertyPath()).willReturn(p2);
        given(v1.getMessage()).willReturn("must not be blank");
        given(v2.getMessage()).willReturn("must be greater than 0");

        given(validator.validate(any())).willReturn(Set.of(v1, v2));

        // Act
        AppException ex = assertThrows(AppException.class, () -> validateRequest.validate(dto));

        // Assert
        assertEquals(BusinessType.GENERIC_INVALID_PARAM, ex.getType());
        String msg = ex.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("name, must not be blank"));
        assertTrue(msg.contains("stock, must be greater than 0"));
    }
}
