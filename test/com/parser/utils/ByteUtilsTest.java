package com.parser.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class ByteUtilsTest {

    @Test
    public void testReadInt() {
        //Length zero should return 0
        assertEquals(0, ByteUtils.readInt(new byte[3], 3, 0));

        // Normal positive integer
        byte[] data = "34=1092;".getBytes(StandardCharsets.US_ASCII);
        assertEquals(1092, ByteUtils.readInt(data, 3, 4));

        // Test zero
        byte[] zeroData = "34=0;".getBytes(StandardCharsets.US_ASCII);
        assertEquals(0, ByteUtils.readInt(zeroData, 3, 1));

        // Test negative integer
        byte[] negativeData = "-50".getBytes();
        assertEquals(-50, ByteUtils.readInt(negativeData, 0, 3));

        // Test invalid input
        assertThrows(NumberFormatException.class, () -> ByteUtils.readInt("34=ABC;".getBytes(StandardCharsets.US_ASCII), 3, 3));
    }

    @Test
    public void testReadDouble() {
        //Length zero should return 0
        assertEquals(0, ByteUtils.readLong(new byte[3], 3, 0));

        // Normal double with decimal
        byte[] data = "44=150.50;".getBytes(StandardCharsets.US_ASCII);
        // Extract "150.50" (starts at index 3, length 6)
        assertEquals(150.50, ByteUtils.readDouble(data, 3, 6), 0.0001);

        // Test negative double
        byte[] negativeData = "-75.25".getBytes();
        assertEquals(-75.25, ByteUtils.readDouble(negativeData, 0, 6), 0.0001);

        // Test whole number as double
        byte[] wholeNumber = "200".getBytes();
        assertEquals(200.0, ByteUtils.readDouble(wholeNumber, 0, 3), 0.0001);
    }

    @Test
    public void testReadLong() {
        //Length zero should return 0
        assertEquals(0, ByteUtils.readLong(new byte[3], 3, 0));

        // Normal long integer
        byte[] data = "1234567890123".getBytes(StandardCharsets.US_ASCII);
        assertEquals(1234567890123L, ByteUtils.readLong(data, 0, 13));

        // Test with a large sequence number common in high-volume days
        byte[] largeSeq = "9223372036854775807".getBytes(); // Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, ByteUtils.readLong(largeSeq, 0, 19));

        // Test negative long
        byte[] negativeData = "-9876543210".getBytes();
        assertEquals(-9876543210L, ByteUtils.readLong(negativeData, 0, 11));

        // Test invalid input
        assertThrows(NumberFormatException.class, () -> ByteUtils.readLong("123ABC".getBytes(StandardCharsets.US_ASCII), 0, 6));

    }

    @Test
    public void testReadBoolean() {
        // True values
        assertTrue(ByteUtils.readBoolean("44=Y;".getBytes(StandardCharsets.US_ASCII), 3));
        assertTrue(ByteUtils.readBoolean("44=y;".getBytes(StandardCharsets.US_ASCII), 3));
        assertTrue(ByteUtils.readBoolean("44=1;".getBytes(StandardCharsets.US_ASCII), 3));

        // False values
        assertFalse(ByteUtils.readBoolean("44=N;".getBytes(StandardCharsets.US_ASCII), 3));
        assertFalse(ByteUtils.readBoolean("44=n;".getBytes(StandardCharsets.US_ASCII), 3));
        assertFalse(ByteUtils.readBoolean("44=0;".getBytes(StandardCharsets.US_ASCII), 3));

        // Invalid value
        assertThrows(IllegalArgumentException.class, ()-> ByteUtils.readBoolean("44=B;".getBytes(StandardCharsets.US_ASCII), 3));
    }

    @Test
    public void testReadBytes() {
        // Normal case
        byte[] src = "SenderID".getBytes();
        byte[] dest = new byte[8];

        int copied = ByteUtils.readBytes(src, 0, 8, dest);

        assertEquals(8, copied);
        assertArrayEquals(src, dest);

        // Partial copy
        byte[] partialDest = new byte[4];
        copied = ByteUtils.readBytes(src, 0, 4, partialDest);
        assertEquals(4, copied);
        assertArrayEquals("Send".getBytes(), partialDest);

        // Zero length copy
        byte[] zeroDest = new byte[4];
        copied = ByteUtils.readBytes(src, 0, 0, zeroDest);
        assertEquals(0, copied);

        // Length exceeds source
        byte[] smallSrc = "ID".getBytes();
        byte[] largeDest = new byte[4];
        copied = ByteUtils.readBytes(smallSrc, 0, 4, largeDest);

        // Should only copy available bytes
        assertEquals(2, copied);
        assertArrayEquals(new byte[] {'I', 'D', 0, 0}, largeDest);

        // length is greater than destinaton array size
        byte[] destSmall = new byte[3];
        assertThrows(IllegalArgumentException.class, () -> ByteUtils.readBytes(src, 0, 8, destSmall));

    }

}
