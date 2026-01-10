package com.parser;

import com.parser.utils.ByteUtils;
import com.parser.utils.FieldLocationMap;
import com.parser.validate.ChecksumValidator;
import com.parser.validate.MessageValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to parse FIX messages received as byte arrays.
 * This parser extracts the location (offset) and length of each FIX tag value
 * in the byte array without copying the values. These offsets can later be used
 * to retrieve specific FIX tag values from the byte array.
 */
public class FixParser {
    private static final byte SOH = 0x01;
    private static final byte EQUALS = '=';
    private static final int DEFAULT_MAXIMUM_FIELDS_EXPECTED = 200;

    private final FieldLocationMap fieldLocationMap;
    private final List<MessageValidator> messageValidators;

    /**
     * Constructs a new FixParser instance with the default maximum number of expected fields.
     */
    public FixParser() {
        this(DEFAULT_MAXIMUM_FIELDS_EXPECTED);
    }

    /**
     * Constructs a new FixParser instance with the specified maximum number of expected fields.
     */
    public FixParser(int maxNumberOfFieldsExpected) {
        this.fieldLocationMap = new FieldLocationMap(maxNumberOfFieldsExpected);
        this.messageValidators = new ArrayList<>() {{
            add(new ChecksumValidator());
        }};
    }

    /**
     * Parses FIX fields from the given byte array.
     * Clears any previously parsed state and extracts offsets and lengths for each tag.
     *
     * @param data The FIX message as a byte array.
     * @return true if parsing is successfully along with validations.
     */
    public boolean parse(byte[] data) {
        this.fieldLocationMap.clear();

        int i = 0;
        while (i < data.length) {
            int fixTagStart = i;
            // Find '=' to get the tag
            while (i < data.length && data[i] != EQUALS)
                i++;
            int tag = ByteUtils.readInt(data, fixTagStart, i - fixTagStart);
            i++; // skip '='
            int fixValStart = i;
            // Find SOH to get the value
            while (i < data.length && data[i] != SOH)
                i++;
            this.fieldLocationMap.put(tag, fixValStart, i - fixValStart);
            i++; // skip SOH
        }
        return validate(data);
    }

    /**
     * Validates the FIX message using all registered validators.
     *
     * @param data The FIX message as a byte array.
     * @return true if all validators pass, false otherwise.
     */
    private boolean validate(byte[] data) {
        boolean result = true;
        for (MessageValidator validator : this.messageValidators) {
            result = result & validator.validate(data, this);
        }
        return result;
    }

    /**
     * Checks if a tag exists in the parsed FIX message.
     *
     * @param tag The FIX tag to check.
     * @return True if the tag exists, false otherwise.
     */
    public boolean fieldDoesNotExists(int tag) {
        return !fieldLocationMap.containsKey(tag);
    }

    /**
     * Ensures the specified tag exists in the parsed data.
     * Throws a RuntimeException if the tag is not found.
     *
     * @param tag The FIX tag to check.
     */
    private void checkFieldExists(int tag) {
        if (fieldDoesNotExists(tag))
            throw new RuntimeException("Tag not found in message");
    }

    /**
     * Retrieves the index of the specified tag in the internal map.
     *
     * @param tag The FIX tag to retrieve.
     * @return The index of the tag.
     */
    private int getIndex(int tag) {
        return fieldLocationMap.getIndex(tag);
    }

    /**
     * Retrieves the offset of the specified tag in the parsed message.
     *
     * @param tag The FIX tag to retrieve.
     * @return The offset of the tag.
     */
    public int getOffset(int tag) {
        return fieldLocationMap.getOffset(getIndex(tag));
    }

    /**
     * Retrieves the length of the specified tag's value in the parsed message.
     *
     * @param tag The FIX tag to retrieve.
     * @return The length of the tag's value.
     */
    public int getLength(int tag) {
        return fieldLocationMap.getLength(getIndex(tag));
    }

    /**
     * Reads an int value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag  The FIX tag to retrieve.
     * @return The int value for the tag.
     */
    public int getInt(byte[] data, int tag) {
        checkFieldExists(tag);

        int index = getIndex(tag);
        return ByteUtils.readInt(data, fieldLocationMap.getOffset(index), fieldLocationMap.getLength(index));
    }


    /**
     * Reads a long value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag  The FIX tag to retrieve.
     * @return The long value for the tag.
     */
    public long getLong(byte[] data, int tag) {
        checkFieldExists(tag);
        int index = getIndex(tag);
        return ByteUtils.readLong(data, fieldLocationMap.getOffset(index), fieldLocationMap.getLength(index));
    }

    /**
     * Reads a double value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag  The FIX tag to retrieve.
     * @return The double value for the tag.
     */
    public double getDouble(byte[] data, int tag) {
        checkFieldExists(tag);
        int index = getIndex(tag);
        return ByteUtils.readDouble(data, fieldLocationMap.getOffset(index), fieldLocationMap.getLength(index));
    }

    /**
     * Reads a boolean value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag  The FIX tag to retrieve.
     * @return The boolean value for the tag.
     */
    public boolean getBoolean(byte[] data, int tag) {
        checkFieldExists(tag);
        int index = getIndex(tag);
        return ByteUtils.readBoolean(data, fieldLocationMap.getOffset(index));
    }

    /**
     * Reads the raw byte array value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag  The FIX tag to retrieve.
     * @return A byte array containing the value for the tag.
     */
    public byte[] getBytes(byte[] data, int tag) {
        checkFieldExists(tag);
        int index = getIndex(tag);
        byte[] values = new byte[fieldLocationMap.getLength(index)];
        ByteUtils.readBytes(data, fieldLocationMap.getOffset(index), fieldLocationMap.getLength(index), values);
        return values;
    }

}