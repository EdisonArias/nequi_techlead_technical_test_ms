package co.com.nequi.teachlead.technical.test.api.shared.util;

import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ValidateRequest {

    private final Validator validator;

    public void requireFranchiseId(String value) {
        if (Boolean.TRUE.equals(valueIsNull(value))) {
            throw BusinessType.NO_MESSAGE_ID.build(value);
        }
    }

    public void requireMessageId(String value) {
        if (Boolean.TRUE.equals(valueIsNull(value))) {
            throw BusinessType.NO_MESSAGE_ID.build();
        }
    }

    private Boolean valueIsNull(String value) {
        if (value == null || value.isBlank()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public <T> void validate(T objects) {
        var error = this.validator.validate(objects).stream()
                .map(violation -> violation.getPropertyPath() + ", " + violation.getMessage()).toList();
        if (Boolean.FALSE.equals(error.isEmpty())) {
            throw BusinessType.GENERIC_INVALID_PARAM.build(error.toString());
        }
    }
}