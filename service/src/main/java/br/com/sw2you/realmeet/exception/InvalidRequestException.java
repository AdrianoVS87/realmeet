package br.com.sw2you.realmeet.exception;

import br.com.sw2you.realmeet.validator.ValidationError;
import br.com.sw2you.realmeet.validator.ValidationErrors;

public class InvalidRequestException extends RuntimeException {
    private final ValidationErrors validationErros;

    public InvalidRequestException(ValidationErrors validationErrors) {
        super(validationErrors.toString());
        this.validationErros = validationErrors;
    }

    public InvalidRequestException(ValidationError validationError) {
        this(new ValidationErrors().add(validationError));
    }

    public ValidationErrors getValidationErros() {
        return validationErros;
    }
}
