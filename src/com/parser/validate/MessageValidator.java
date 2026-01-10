package com.parser.validate;

import com.parser.FixParser;

/**
 * Interface for validating FIX messages.
 * Implementations of this interface should provide logic to validate
 * a FIX message using the provided FixParser instance.
 */
public interface MessageValidator {

    /**
     * Validates a FIX message.
     *
     * @param data The FIX message as a byte array.
     * @param fixparser The FixParser instance used for parsing and validation.
     * @return True if the message is valid, false otherwise.
     */
    boolean validate(byte[] data, FixParser fixparser);
}
