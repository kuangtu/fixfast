package org.openfast.template.type.codec;

import org.openfast.test.OpenFastTestCase;

public class SignedIntegerTest extends OpenFastTestCase {

    public void testEncodeDecode() {
        assertEncodeDecode(i(63), "10111111", TypeCodec.INTEGER);
        assertEncodeDecode(i(64), "00000000 11000000", TypeCodec.INTEGER);
        assertEncodeDecode(i(-1), "11111111", TypeCodec.INTEGER);
        assertEncodeDecode(i(-2), "11111110", TypeCodec.INTEGER);
        assertEncodeDecode(i(-64), "11000000", TypeCodec.INTEGER);
        assertEncodeDecode(i(-65), "01111111 10111111", TypeCodec.INTEGER);
        assertEncodeDecode(i(639), "00000100 11111111", TypeCodec.INTEGER);
        assertEncodeDecode(i(942755), "00111001 01000101 10100011", TypeCodec.INTEGER);
        assertEncodeDecode(i(-942755), "01000110 00111010 11011101", TypeCodec.INTEGER);
        assertEncodeDecode(i(8193), "00000000 01000000 10000001", TypeCodec.INTEGER);
        assertEncodeDecode(i(-8193), "01111111 00111111 11111111", TypeCodec.INTEGER);
    }
    
    public void testEncodeDecodeBoundary() {
        assertEncodeDecode(l(Long.MAX_VALUE), "00000000 01111111 01111111 01111111 01111111 01111111 01111111 01111111 01111111 11111111", TypeCodec.INTEGER);
        assertEncodeDecode(l(Long.MIN_VALUE), "01111111 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 10000000", TypeCodec.INTEGER);
    }

}
