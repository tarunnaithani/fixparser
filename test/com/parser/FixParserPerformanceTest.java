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

        byte[] rawFix = "8=FIX.4.2\u00019=178\u000135=8\u000149=PHLX\u000156=PERS\u000152=20071123-05:30:00.000\u000111=ATOMNOCCC9990900\u000120=3\u0001150=E\u000139=E\u000155=MSFT\u0001167=CS\u000154=1\u000138=15\u000140=2\u000144=15\u000158=PHLX EQUITY TESTING\u000159=0\u000147=C\u000132=0\u000131=0\u0001151=15\u000114=0\u00016=0\u000110=128\u0001".getBytes(StandardCharsets.US_ASCII);
        final int iterations = 1_000_000;

        long startTime = System.nanoTime();
        for(int i=0; i < iterations; i++)
            Assert.assertTrue(fixParser.parse(rawFix));

        long endTime = System.nanoTime();
        long elapsedNanos = endTime - startTime;
        long elapsedMs = elapsedNanos / 1_000_000;

        System.out.println("Parsed " + iterations + " messages in " + elapsedMs + " ms");
        System.out.println("Parsed(avg) single message in " + (elapsedNanos)/iterations + " ns");

        // Adjust threshold if needed for your environment.
        Assert.assertTrue ("Parsing of single message too slow(> 1 micro)" + elapsedNanos/iterations,  elapsedNanos/iterations< 500);
        Assert.assertTrue("Parsing too slow: " + elapsedMs + " ms", elapsedMs < 400);
    }
}
