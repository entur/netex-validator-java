package org.entur.netex.validation.exception;

import org.entur.netex.validation.exception.NetexValidationException;

public class RetryableNetexValidationException extends NetexValidationException {
    public RetryableNetexValidationException(Throwable t) {
        super(t);
    }
}
