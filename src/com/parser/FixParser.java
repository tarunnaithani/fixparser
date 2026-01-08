package com.parser;

import com.parser.utils.ByteUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to parse fix message received as byte array.
 * It runs extraction of location/offset of each fix tag value
 * along with number of bytes used to store the value.
 * These offsets can later be used to read specific fix tag from byte array.
 */
public class FixParser {
    private static final byte SOH = 0x01;
    private static final byte EQUALS = '=';

    private final Map<Integer, FixField> fixfields;

    public FixParser() {
        this.fixfields = new HashMap<>();
    }

    /**
     * Method to parse fix fields from FIX message in byte array.
     * It makes extracts the offsets in the byte array value for each tag along with
     * number of bytes that store the value
     *
     * @param data fix message as byte array
     * @return number of fix fields parsed successfully
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
     * Check if tag exists in parsed tags from fix message
     *
     * @param tag FIX tag to be retrieved
     * @return true if tag is present in parsed tags
     */
    public boolean fieldExists(int tag){
        return fixfields.get(tag) != null;
    }

    /**
     *  Retrieves field object containing tag location
     *
     * @param tag FIX tag to be retrieved
     * @return FixField containing offset and length of the value
     */
    private FixField getField(int tag){
        return fixfields.get(tag);
    }

    /**
     *  check if the tag exists else throw exception
     * @param tag FIX tag to be checked
     */
    private void checkFieldExists(int tag) {
        if(!fieldExists(tag))
            throw new RuntimeException("Tag not found in message");
    }

    /**
     * read int value for a FIX tag
     *
     * @param data The raw FIX message byte array
     * @param tag FIX tag to be retrieved
     * @return int value for the tag from message
     */
    public int getInt(byte[] data, int tag){
        checkFieldExists(tag);

        FixField f =  getField(tag);
        return ByteUtils.readInt(data, f.offset, f.length);
    }


    /**
     * read long value for a FIX tag
     *
     * @param data The raw FIX message byte array
     * @param tag FIX tag to be retrieved
     * @return long value read from bytes
     */
    public long getLong(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readLong(data, f.offset, f.length);
    }
    /**
     *
     * read double value for a FIX tag
     *
     * @param data The raw FIX message byte array
     * @param tag FIX tag to be retrieved
     * @return double value read from bytes
     */
    public double getDouble(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readDouble(data, f.offset, f.length);
    }

    /**
     * read boolean value for a FIX tag
     *
     * @param data The raw FIX message byte array
     * @param tag FIX tag to be retrieved
     * @return boolean value read
     */
    public boolean getBoolean(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        return ByteUtils.readBoolean(data, f.offset);
    }

    /**
     * read byte array value for a FIX tag
     *
     * @param data The raw FIX message byte array
     * @param tag FIX tag to be retrieved
     * @return byte array containing values for the tag
     */
    public byte[] getBytes(byte[] data, int tag){
        checkFieldExists(tag);
        FixField f =  getField(tag);
        byte[] values = new byte[f.length];
        ByteUtils.readBytes(data, f.offset, f.length, values);
        return values;
    }

    static class FixField {
        final int offset, length;

        FixField(int offset, int length) {
            this.offset = offset;
            this.length = length;
        }
    }
}