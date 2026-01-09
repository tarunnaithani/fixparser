package com.parser;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class FixParserPerformanceTest {

    @Test
    public void testMultipleFixMessageParsing() {
        FixParser fixParser = new FixParser();

        byte[] rawFix = "8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TESTBUY1\u000152=20260107-18:14:19.508\u000156=TESTSELL1\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u000110=092\u0001".getBytes(StandardCharsets.US_ASCII);
        final int iterations = 1_000_000;

        long startTime = System.nanoTime();
        for(int i=0; i < iterations; i++)
            assert fixParser.parse(rawFix) == 16;

        long endTime = System.nanoTime();
        long elapsedNanos = endTime - startTime;
        long elapsedMs = elapsedNanos / 1_000_000;

        System.out.println("Parsed " + iterations + " messages in " + elapsedMs + " ms");
        System.out.println("Parsed(avg) single message in " + (elapsedNanos)/iterations + " ns");

        // Adjust threshold if needed for your environment.
        Assert.assertTrue ("Parsing of single message too slow(> 1 micro)" + elapsedNanos/iterations,  elapsedNanos/iterations< 500);
        Assert.assertTrue("Parsing too slow: " + elapsedMs + " ms", elapsedMs < 250);
    }
}
