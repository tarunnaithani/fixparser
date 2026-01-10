package com.parser.validate;

import com.parser.FixParser;


/**
 * A validator for verifying the checksum of a FIX message.
 * This implementation ensures that the checksum value in the "10=" tag matches
 * the computed checksum of the message.
 */
public class ChecksumValidator implements MessageValidator{

    private static final byte SOH = 0x01;

    /**
     * Validates the checksum of a FIX message.
     * Computes the checksum by summing all bytes up to the "10=" tag and comparing
     * it to the declared checksum value in the message.
     *
     * @param data The FIX message as a byte array.
     * @param fixparser The FixParser instance used for parsing the message.
     * @return True if the checksum is valid, false otherwise.
     */
    public boolean validate(byte[] data, FixParser fixparser) {
        try {

            if (fixparser.fieldDoesNotExists(10)) return false;

            // read declared checksum value from message
            int declared = fixparser.getInt(data, 10);

            // find start of the tag (scan backwards to previous SOH or start)
            int valueOffset = fixparser.getOffset(10);
            int tagStart = valueOffset - 1;
            while (tagStart >= 0 && data[tagStart] != SOH) {
                tagStart--;
            }
            // tagFieldStart = tagStart + 1; we want sum up to tagFieldStart - 1 == tagStart
            int sumEnd = tagStart;

            int sum = 0;
            for (int i = 0; i <= sumEnd; i++) {
                sum += (data[i] & 0xFF);
            }
            int expected = sum % 256;
            return expected == declared;
        } catch (Exception e) {
            return false;
        }
    }
}
