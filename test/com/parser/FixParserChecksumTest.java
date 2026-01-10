package com.parser;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.charset.StandardCharsets;

@RunWith(JUnit4.class)
public class FixParserChecksumTest {
    private final FixParser fixparser = new FixParser();

    @Test
    public void validateChecksumReturnsTrueForValidMessage() {
        byte[] rawFix = ("8=FIX.4.2\u00019=178\u000135=8\u000149=PHLX\u000156=PERS\u000152=20071123-05:30:00.000\u000111=ATOMNOCCC9990900\u000120=3\u0001150=E\u000139=E\u000155=MSFT\u0001167=CS\u000154=1\u000138=15\u000140=2\u000144=15\u000158=PHLX EQUITY TESTING\u000159=0\u000147=C\u000132=0\u000131=0\u0001151=15\u000114=0\u00016=0\u000110=128\u0001")
                .getBytes(StandardCharsets.US_ASCII);
        Assert.assertTrue(fixparser.parse(rawFix));
    }

    @Test
    public void validateChecksumReturnsFalseForInvalidChecksumValue() {
        byte[] rawFix = ("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TEST1111\u000152=20260107-18:14:19.508\u000156=TESTLEG11\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u000110=093\u0001")
                .getBytes(StandardCharsets.US_ASCII);
        Assert.assertFalse(fixparser.parse(rawFix));
    }

    @Test
    public void validateChecksumReturnsFalseWhenChecksumMissing() {
        byte[] rawFix = ("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TEST1111\u000152=20260107-18:14:19.508\u000156=TESTLEG11\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u0001")
                .getBytes(StandardCharsets.US_ASCII);
        Assert.assertFalse(fixparser.parse(rawFix));
    }

    @Test
    public void validateChecksumReturnsFalseOnNonNumericChecksum() {
        byte[] rawFix = ("8=FIX.4.4\u00019=148\u000135=D\u000134=1080\u000149=TEST1111\u000152=20260107-18:14:19.508\u000156=TESTLEG11\u000111=636730640278898634\u000115=USD\u000121=2\u000138=7000\u000140=1\u000154=1\u000155=MSFT\u000160=20260107-18:14:19.492\u000110=ABC\u0001")
                .getBytes(StandardCharsets.US_ASCII);
        Assert.assertFalse(fixparser.parse(rawFix));
    }
}