package com.parser;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class FixParserPerformanceTest {
    int MAX_ITERATIONS = 1_000_000;
    @Test
    public void testParse() {
        FixParser fixParser = new FixParser();

        byte[] rawFix = "8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TESTBUY1\u000152=20260107-18:14:19.508\u000156=TESTSELL1\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u000110=092\u0001".getBytes(StandardCharsets.US_ASCII);
        long startTime = System.nanoTime();
        for(int i=0; i < MAX_ITERATIONS; i++){
            assert fixParser.parse(rawFix) == 16;
        }
        long endTime = System.nanoTime();
        System.out.println("Total messages parsed - " + MAX_ITERATIONS + ", total time(in nanos)-" + ((endTime - startTime))  + ", time taken per message(in nanos) -" + (endTime - startTime)/MAX_ITERATIONS);
        assert (endTime - startTime)/MAX_ITERATIONS < 1000;
    }
}
