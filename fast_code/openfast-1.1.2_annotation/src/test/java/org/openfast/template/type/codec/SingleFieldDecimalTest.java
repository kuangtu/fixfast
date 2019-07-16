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


package org.openfast.template.type.codec;

import org.openfast.DecimalValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;

import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;


public class SingleFieldDecimalTest extends OpenFastTestCase {
    public void testEncodeDecodeBoundary() {
        assertEncodeDecode(new DecimalValue(Long.MAX_VALUE, 63), "10111111 00000000 01111111 01111111 01111111 01111111 01111111 01111111 01111111 01111111 11111111", TypeCodec.SF_SCALED_NUMBER);
        assertEncodeDecode(new DecimalValue(Long.MIN_VALUE, -63), "11000001 01111111 00000000 00000000 00000000 00000000 00000000 00000000 00000000 00000000 10000000", TypeCodec.SF_SCALED_NUMBER);
    }
    
    public void testEncodeDecode() {
        assertEncodeDecode(94275500, "10000010 00111001 01000101 10100011");
        assertEncodeDecode(9427.55, "11111110 00111001 01000101 10100011");
        assertEncodeDecode(4, "10000000 10000100");
        assertEncodeDecode(400, "10000010 10000100");
        assertEncodeDecode(0.4, "11111111 10000100");
        assertEncodeDecode(1000, "10000011 10000001");
        assertEncodeDecode(d(9427550, 1),
            "10000001 00000100 00111111 00110100 11011110",
            TypeCodec.SF_SCALED_NUMBER);
    }

    public void testEncodeLargeDecimalReportsError() {
        try {
            TypeCodec.SF_SCALED_NUMBER.encode(d(150, 64));
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.R1_LARGE_DECIMAL, e.getCode());
            assertEquals("Encountered exponent of size 64", e.getMessage());
        }
    }

    public void testDecodeLargeDecimalReportsError() {
        try {
            TypeCodec.SF_SCALED_NUMBER.decode(bitStream("00000001 11111111 10000001"));
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.R1_LARGE_DECIMAL, e.getCode());
            assertEquals("Encountered exponent of size 255", e.getMessage());
        }
    }

    private void assertEncodeDecode(double value, String bitString) {
        assertEncodeDecode(d(value), bitString, TypeCodec.SF_SCALED_NUMBER);
    }
}
