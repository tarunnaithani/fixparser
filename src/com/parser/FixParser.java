package com.parser;

import com.parser.utils.ByteUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse FIX messages received as byte arrays.
 * This parser extracts the location (offset) and length of each FIX tag value
 * in the byte array without copying the values. These offsets can later be used
 * to retrieve specific FIX tag values from the byte array.
 */
public class FixParser {
    private static final byte SOH = 0x01;
    private static final byte EQUALS = '=';

    private final Map<Integer, FixField> fixfields;

    /**
     * Constructs a new FixParser instance with an empty internal map.
     */
    public FixParser() {
        this.fixfields = new HashMap<>();
    }

    /**
     * Parses FIX fields from the given byte array.
     * Clears any previously parsed state and extracts offsets and lengths for each tag.
     *
     * @param data The FIX message as a byte array.
     * @return The number of FIX fields parsed successfully.
     */
    public int parse(byte[] data) {
        this.fixfields.clear();

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
            this.fixfields.put(tag, new FixField(fixValStart, i - fixValStart));
            i++; // skip SOH
        }
        return fixfields.size();
    }

    /**
     * Checks if a tag exists in the parsed FIX message.
     *
     * @param tag The FIX tag to check.
     * @return True if the tag exists, false otherwise.
     */
    public boolean fieldExists(int tag){
        return fixfields.get(tag) != null;
    }

    /**
     * Retrieves the FixField object containing the offset and length of the tag value.
     *
     * @param tag The FIX tag to retrieve.
     * @return The FixField object, or null if the tag does not exist.
     */
    private FixField getField(int tag){
        return fixfields.get(tag);
    }

    /**
     * Ensures the specified tag exists in the parsed data.
     * Throws a RuntimeException if the tag is not found.
     *
     * @param tag The FIX tag to check.
     */
    private void checkFieldExists(int tag) {
        if(!fieldExists(tag))
            throw new RuntimeException("Tag not found in message");
    }

    /**
     * Reads an int value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag The FIX tag to retrieve.
     * @return The int value for the tag.
     */
    public int getInt(byte[] data, int tag){
        checkFieldExists(tag);

        FixField f =  getField(tag);
        return ByteUtils.readInt(data, f.offset, f.length);
    }

    /**
     * Reads a long value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag The FIX tag to retrieve.
     * @return The long value for the tag.
     */
    public long getLong(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readLong(data, f.offset, f.length);
    }

    /**
     * Reads a double value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag The FIX tag to retrieve.
     * @return The double value for the tag.
     */
    public double getDouble(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readDouble(data, f.offset, f.length);
    }

    /**
     * Reads a boolean value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag The FIX tag to retrieve.
     * @return The boolean value for the tag.
     */
    public boolean getBoolean(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readBoolean(data, f.offset);
    }

    /**
     * Reads the raw byte array value for the specified FIX tag.
     *
     * @param data The raw FIX message byte array.
     * @param tag The FIX tag to retrieve.
     * @return A byte array containing the value for the tag.
     */
    public byte[] getBytes(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        byte[] values = new byte[f.length];
        ByteUtils.readBytes(data, f.offset, f.length, values);
        return values;
    }

    /**
     * Internal class to store the offset and length of a FIX tag value.
     */
    static class FixField {
        final int offset, length;

        /**
         * Constructs a FixField with the specified offset and length.
         *
         * @param offset The offset of the tag value.
         * @param length The length of the tag value.
         */
        FixField(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }
}