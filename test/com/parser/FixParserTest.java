package com.parser;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class FixParserTest {
    FixParser fixparser = new FixParser();

    @Test
    public void testParsedFixMessage() {
        byte[] rawFix = "8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TEST1111\u000152=20260107-18:14:19.508\u000156=TESTLEG11\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u000110=092\u0001".getBytes(StandardCharsets.US_ASCII);

        fixparser.parse(rawFix);
        Assert.assertEquals("FIX.4.4", str(fixparser.getBytes(rawFix, 8)));
        Assert.assertEquals(148, fixparser.getInt(rawFix, 9));
        Assert.assertEquals("D", str(fixparser.getBytes(rawFix, 35)));

        Assert.assertEquals(1080, fixparser.getInt(rawFix, 34));
        Assert.assertEquals("TEST1111", str(fixparser.getBytes(rawFix, 49)));
        Assert.assertEquals("20260107-18:14:19.508", str(fixparser.getBytes(rawFix, 52)));
        Assert.assertEquals("TESTLEG11", str(fixparser.getBytes(rawFix, 56)));
        Assert.assertEquals(636730640278898634L, fixparser.getLong(rawFix, 11));
        Assert.assertEquals("USD", str(fixparser.getBytes(rawFix, 15)));
        Assert.assertEquals(2, fixparser.getInt(rawFix, 21));
        Assert.assertEquals(7000.0, fixparser.getDouble(rawFix, 38), 0.0001);
        Assert.assertTrue(fixparser.getBoolean(rawFix, 40));
        Assert.assertTrue(fixparser.getBoolean(rawFix, 54));
        Assert.assertEquals("MSFT", str(fixparser.getBytes(rawFix, 55)));
        Assert.assertEquals("20260107-18:14:19.492", str(fixparser.getBytes(rawFix, 60)));
        Assert.assertEquals(92, fixparser.getInt(rawFix, 10));

    }

    private String str(byte[] data){
        return new String(data, StandardCharsets.UTF_8);
    }
}
