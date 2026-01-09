package com.parser;

import com.parser.utils.ByteUtils;
import com.parser.utils.FieldLocationsMap;

/**
 * Class to parse FIX messages received as byte arrays.
 * This parser extracts the location (offset) and length of each FIX tag value
 * in the byte array without copying the values. These offsets can later be used
 * to retrieve specific FIX tag values from the byte array.
 */
public class FixParser {
    private static final byte SOH = 0x01;
    private static final byte EQUALS = '=';

    private final FieldLocationsMap fieldLocationsMap;

    /**
     * Constructs a new FixParser instance with an empty internal map.
     */
    public FixParser() {
        this.fieldLocationsMap = new FieldLocationsMap();
    }

    /**
     * Parses FIX fields from the given byte array.
     * Clears any previously parsed state and extracts offsets and lengths for each tag.
     *
     * @param data The FIX message as a byte array.
     * @return The number of FIX fields parsed successfully.
     */
    public int parse(byte[] data) {
        this.fieldLocationsMap.clear();

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
            this.fieldLocationsMap.put(tag, fixValStart, i - fixValStart);
            i++; // skip SOH
        }
        return fieldLocationsMap.size();
    }

    /**
     * Checks if a tag exists in the parsed FIX message.
     *
     * @param tag The FIX tag to check.
     * @return True if the tag exists, false otherwise.
     */
    public boolean fieldExists(int tag) {
        return fieldLocationsMap.containsKey(tag);
    }

    /**
     * Ensures the specified tag exists in the parsed data.
     * Throws a RuntimeException if the tag is not found.
     *
     * @param tag The FIX tag to check.
     */
    private void checkFieldExists(int tag) {
        if (!fieldExists(tag))
            throw new RuntimeException("Tag not found in message");
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

        int index = fieldLocationsMap.getIndex(tag);
        return ByteUtils.readInt(data, fieldLocationsMap.getOffset(index), fieldLocationsMap.getLength(index));
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
        int index = fieldLocationsMap.getIndex(tag);
        return ByteUtils.readLong(data, fieldLocationsMap.getOffset(index), fieldLocationsMap.getLength(index));
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
        int index = fieldLocationsMap.getIndex(tag);
        return ByteUtils.readDouble(data, fieldLocationsMap.getOffset(index), fieldLocationsMap.getLength(index));
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
        int index = fieldLocationsMap.getIndex(tag);
        return ByteUtils.readBoolean(data, fieldLocationsMap.getOffset(index));
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
        int index = fieldLocationsMap.getIndex(tag);
        byte[] values = new byte[fieldLocationsMap.getLength(index)];
        ByteUtils.readBytes(data, fieldLocationsMap.getOffset(index), fieldLocationsMap.getLength(index), values);
        return values;
    }
}