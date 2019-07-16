package org.openfast.template.type.codec;

import org.openfast.test.OpenFastTestCase;

public class UnsignedIntegerTest extends OpenFastTestCase {

	public void testEncodeDecode() {
        assertEncodeDecode(i(127),   "11111111", TypeCodec.UINT);
        assertEncodeDecode(i(16383), "01111111 11111111", TypeCodec.UINT);
        assertEncodeDecode(i(5),     "10000101", TypeCodec.UINT);
        assertEncodeDecode(i(0),     "10000000", TypeCodec.UINT);
        assertEncodeDecode(i(942755), "00111001 01000101 10100011", TypeCodec.UINT);
        assertEncodeDecode(i(268435452), "01111111 01111111 01111111 11111100", TypeCodec.UINT);
        assertEncodeDecode(i(269435452), "00000001 00000000 00111101 00000100 10111100", TypeCodec.UINT);
        assertEncodeDecode(l(274877906943L), "00000111 01111111 01111111 01111111 01111111 11111111", TypeCodec.UINT);
        assertEncodeDecode(l(1181048340000L), "00100010 00101111 01011111 01011101 01111100 10100000", TypeCodec.UINT);
        
        assertEncodeDecode(l(4294967295L), "00001111 01111111 01111111 01111111 11111111", TypeCodec.UINT);
    }

}
