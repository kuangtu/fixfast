/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
*/


package org.openfast;

import org.openfast.test.TestUtil;

import junit.framework.TestCase;


public class BitVectorTest extends TestCase {
    public void testGetTruncatedBytes() {
        BitVector vector = new BitVector(new byte[] { 0x00, 0x00 });
        assertTrue(vector.isOverlong());
        TestUtil.assertByteArrayEquals(new byte[] { (byte) 0x80 },
            vector.getTruncatedBytes());

        vector = new BitVector(new byte[] { 0x00 });
        assertFalse(vector.isOverlong());
        TestUtil.assertByteArrayEquals(new byte[] { (byte) 0x80 },
            vector.getTruncatedBytes());

        vector = new BitVector(new byte[] { 0x60, 0x00, 0x04, 0x00 });
        assertTrue(vector.isOverlong());
        TestUtil.assertByteArrayEquals(new byte[] { 0x60, 0x00, (byte) 0x84 },
            vector.getTruncatedBytes());
    }

    /*
     * Test method for 'org.openfast.BitVector.getBytes()'
     */
    public void testGetBytes() {
        BitVector vector = new BitVector(7);
        assertEquals(1, vector.getBytes().length);
        vector = new BitVector(8);
        assertEquals(2, vector.getBytes().length);
    }

    /*
     * Test method for 'org.openfast.BitVector.set(int)'
     */
    public void testSetWithOneByte() {
        BitVector vector = new BitVector(7);
        vector.set(0);
        TestUtil.assertBitVectorEquals("11000000", vector.getBytes());
        vector.set(3);
        TestUtil.assertBitVectorEquals("11001000", vector.getBytes());
        vector.set(6);
        TestUtil.assertBitVectorEquals("11001001", vector.getBytes());
    }

    public void testIsSet() {
        BitVector vector = new BitVector(7);
        assertFalse(vector.isSet(1));
        vector.set(1);
        assertTrue(vector.isSet(1));
        assertFalse(vector.isSet(6));
        vector.set(6);
        assertTrue(vector.isSet(6));
        assertFalse(vector.isSet(7));
        assertFalse(vector.isSet(8));
    }

    /*
     * Test method for 'org.openfast.BitVector.set(int)'
     */
    public void testSetWithMultipleBytes() {
        BitVector vector = new BitVector(15);
        vector.set(0);
        TestUtil.assertBitVectorEquals("01000000 00000000 10000000",
            vector.getBytes());
        vector.set(4);
        TestUtil.assertBitVectorEquals("01000100 00000000 10000000",
            vector.getBytes());
        vector.set(9);
        TestUtil.assertBitVectorEquals("01000100 00010000 10000000",
            vector.getBytes());
        vector.set(14);
        TestUtil.assertBitVectorEquals("01000100 00010000 11000000",
            vector.getBytes());
    }

    public void testEquals() {
        BitVector expected = new BitVector(new byte[] { (byte) 0xf0 });
        BitVector actual = new BitVector(7);
        actual.set(0);
        actual.set(1);
        actual.set(2);
        assertEquals(expected, actual);
    }
    
    public void testIndexLastSet() {
    	BitVector bv = new BitVector(new byte[] { 0x70, 0x00, 0x04 });
    	assertEquals(18, bv.indexOfLastSet());
    }
}
