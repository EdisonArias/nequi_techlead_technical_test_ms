package co.com.nequi.teachlead.technical.test.model.shared.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Formats {

    EXCEPTION_MESSAGE("%s :: %s");

    private final String format;
}
