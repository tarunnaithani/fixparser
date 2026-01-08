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
        byte[] data = "34=1092;".getBytes(StandardCharsets.US_ASCII);
        assertEquals(1092, ByteUtils.readInt(data, 3, 4));

        byte[] zeroData = "34=0;".getBytes(StandardCharsets.US_ASCII);
        assertEquals(0, ByteUtils.readInt(zeroData, 3, 1));

        byte[] negativeData = "-50".getBytes();
        assertEquals(-50, ByteUtils.readInt(negativeData, 0, 3));
    }

    @Test
    public void testReadLong() {
        byte[] data = "1234567890123".getBytes(StandardCharsets.US_ASCII);
        assertEquals(1234567890123L, ByteUtils.readLong(data, 0, 13));

        // Test with a large sequence number common in high-volume days
        byte[] largeSeq = "9223372036854775807".getBytes(); // Long.MAX_VALUE
        assertEquals(Long.MAX_VALUE, ByteUtils.readLong(largeSeq, 0, 19));
    }

    @Test
    public void testReadDouble() {
        byte[] data = "44=150.50;".getBytes(StandardCharsets.US_ASCII);
        // Extract "150.50" (starts at index 3, length 6)
        assertEquals(150.50, ByteUtils.readDouble(data, 3, 6), 0.0001);

        byte[] wholeNumber = "200".getBytes();
        assertEquals(200.0, ByteUtils.readDouble(wholeNumber, 0, 3), 0.0001);
    }

    @Test
    public void testReadBoolean() {
        assertTrue(ByteUtils.readBoolean("44=Y;".getBytes(StandardCharsets.US_ASCII), 3));
        assertTrue(ByteUtils.readBoolean("44=y;".getBytes(StandardCharsets.US_ASCII), 3));
        assertTrue(ByteUtils.readBoolean("44=1;".getBytes(StandardCharsets.US_ASCII), 3));

        assertFalse(ByteUtils.readBoolean("44=N;".getBytes(StandardCharsets.US_ASCII), 3));
        assertFalse(ByteUtils.readBoolean("44=n;".getBytes(StandardCharsets.US_ASCII), 3));
        assertFalse(ByteUtils.readBoolean("44=0;".getBytes(StandardCharsets.US_ASCII), 3));

        byte[] wholeNumber = "200".getBytes();
        assertEquals(200.0, ByteUtils.readDouble(wholeNumber, 0, 3), 0.0001);
    }

    @Test
    public void testReadBytes() {
        byte[] src = "SenderID".getBytes();
        byte[] dest = new byte[8];

        int copied = ByteUtils.readBytes(src, 0, 8, dest);

        assertEquals(8, copied);
        assertArrayEquals(src, dest);
    }


}
