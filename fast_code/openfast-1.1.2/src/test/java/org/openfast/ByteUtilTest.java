package org.openfast;

import org.openfast.test.OpenFastTestCase;

public class ByteUtilTest extends OpenFastTestCase {
    public void testCombine() {
        assertEquals("00000000 01111111", ByteUtil.combine(new byte[] { 0x00 }, new byte[] { 0x7f }));
        assertEquals("00000000 01000000 01111111 00111111", ByteUtil.combine(new byte[] { 0x00, 0x40 }, new byte[] { 0x7f, 0x3f }));
        assertEquals("00000000", ByteUtil.combine(new byte[] { 0x00 }, new byte[] {}));
        assertEquals("01111111", ByteUtil.combine(new byte[] {}, new byte[] { 0x7f }));
    }
    
    public void testCombineOffset() {
        assertEquals("00000000 01111111", ByteUtil.combine(new byte[] { 0x00 , 0x01}, 0, 1, new byte[] { 0x7f }, 0, 2));
//        assertEquals("00000000 01000000 01111111 00111111", ByteUtil.combine(new byte[] { 0x00, 0x40 }, new byte[] { 0x7f, 0x3f }));
//        assertEquals("00000000", ByteUtil.combine(new byte[] { 0x00 }, new byte[] {}));
//        assertEquals("01111111", ByteUtil.combine(new byte[] {}, new byte[] { 0x7f }));
    }
}
