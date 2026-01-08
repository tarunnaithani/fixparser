package com.parser.utils;



/**
 * Utility class for parsing and extracting primitive data types from byte arrays.
 * Provides methods to read integers, longs, doubles, booleans, and raw byte arrays
 * from message represented as a byte array.
 */
public class ByteUtils {

    /**
     * Extracts an int from a byte array.
     *
     * @param data   The raw FIX message byte array.
     * @param offset The starting index of the integer value.
     * @param length The number of bytes to read.
     * @return The parsed integer.
     * @throws NumberFormatException if the bytes contain invalid numeric characters.
     */
    public static int readInt(byte[] data, int offset, int length) {
        if (length <= 0) return 0;

        int result = 0;
        boolean isNegative = false;
        int i = offset;
        int end = offset + length;

        // 1. Handle Negative Sign
        if (data[i] == '-') {
            isNegative = true;
            i++;
        }

        // 2. Accumulate digits
        while (i < end) {
            byte b = data[i++];
            // Convert ASCII byte to numeric value (e.g., '1' is 49, '1'-'0' is 1)
            if (b >= '0' && b <= '9') {
                result = (result * 10) + (b - '0');
            } else {
                // Optional: Handle or throw error for non-numeric characters
                throw new NumberFormatException("Invalid character in integer field");
            }
        }

        return isNegative ? -result : result;
    }


    /**
     * Extracts a double from a byte array.
     * Handles negative numbers and decimal points.
     *
     * @param data   The raw FIX message byte array.
     * @param offset The starting index of the double value.
     * @param length The number of bytes to read.
     * @return The parsed double.
     */
    public static double readDouble(byte[] data, int offset, int length) {
        if (length <= 0) return 0.0;

        double result = 0.0;
        boolean isNegative = false;
        int i = offset;
        int end = offset + length;

        // 1. Handle negative sign
        if (data[i] == '-') {
            isNegative = true;
            i++;
        }

        // 2. Parse integer part
        while (i < end && data[i] != '.') {
            result = result * 10 + (data[i] - '0');
            i++;
        }

        // 3. Parse fractional part
        if (i < end && data[i] == '.') {
            i++; // skip '.'
            double divisor = 10.0;
            while (i < end) {
                result += (data[i] - '0') / divisor;
                divisor *= 10.0;
                i++;
            }
        }

        return isNegative ? -result : result;
    }

    /**
     * Extracts a long from a byte array.
     *
     * @param data   The raw message byte array.
     * @param offset The starting index of the value.
     * @param length The number of bytes to read.
     * @return The parsed long value.
     * @throws NumberFormatException if the bytes contain invalid numeric characters.
     */
    public static long readLong(byte[] data, int offset, int length) {
        if (length <= 0) return 0L;

        long result = 0;
        boolean isNegative = false;
        int i = offset;
        int end = offset + length;

        // 1. Handle sign
        if (data[i] == '-') {
            isNegative = true;
            i++;
        } else if (data[i] == '+') {
            i++;
        }

        // 2. Accumulate digits
        while (i < end) {
            byte b = data[i++];
            if (b < '0' || b > '9') {
                throw new NumberFormatException("Invalid byte in long conversion: " + b);
            }

            // Multiply by 10 and add the numeric value of the ASCII character
            result = (result << 3) + (result << 1) + (b - '0');
        }

        return isNegative ? -result : result;
    }

    /**
     * Reads a boolean value from a byte array.
     * Interprets 'Y', 'y', or '1' as true, and 'N', 'n', or '0' as false.
     *
     * @param data   The raw FIX message byte array.
     * @param offset The index of the boolean value.
     * @return The parsed boolean value.
     * @throws RuntimeException if the byte does not represent a valid boolean.
     */
    public static boolean readBoolean(byte[] data, int offset) {

        if (data[offset] == 'Y' || data[offset] == 'y' || data[offset] == '1') {
            return true;
        } else if (data[offset] == 'N' || data[offset] == 'n' || data[offset] == '0') {
            return false;
        }
        else
            throw new RuntimeException("Not a boolean-" + data[offset]);
    }

    /**
     * Copies bytes from the source array to a pre-allocated destination.
     *
     * @param src    The raw FIX message buffer.
     * @param offset Start index of the field.
     * @param length Length of the field.
     * @param dest   A reusable pre-allocated byte array.
     * @return The number of bytes actually copied.
     * @throws IllegalArgumentException if the destination buffer is too small.
     */
    public static int readBytes(byte[] src, int offset, int length, byte[] dest) {
        if (length > dest.length) {
            throw new IllegalArgumentException("Destination buffer too small");
        }
        System.arraycopy(src, offset, dest, 0, length);
        return length;
    }

}
