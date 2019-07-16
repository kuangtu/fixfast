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
package org.openfast.template.type;

import org.openfast.codec.FastDecoder;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.MessageTemplate;
import org.openfast.template.type.codec.IntegerCodec;
import org.openfast.test.OpenFastTestCase;

public class IntegerTypeTest extends OpenFastTestCase {
    public void testGetSignedIntegerSize() {
        assertEquals(1, IntegerCodec.getSignedIntegerSize(63));
        assertEquals(1, IntegerCodec.getSignedIntegerSize(-64));
        assertEquals(2, IntegerCodec.getSignedIntegerSize(64));
        assertEquals(2, IntegerCodec.getSignedIntegerSize(8191));
        assertEquals(2, IntegerCodec.getSignedIntegerSize(-8192));
        assertEquals(2, IntegerCodec.getSignedIntegerSize(-65));
        assertEquals(4, IntegerCodec.getSignedIntegerSize(134217727));
        assertEquals(4, IntegerCodec.getSignedIntegerSize(-134217728));
    }
    public void testIntegerSizeTooLarge() {
        MessageTemplate template = template("<template>" + "  <uInt32 name=\"price\"/>" + "</template>");
        FastDecoder decoder = decoder("11000000 10000001 00111111 01111111 01111111 01111111 11111111", template);
        try {
            decoder.readMessage();
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D2_INT_OUT_OF_RANGE, e.getCode());
            assertEquals("The value 17179869183 is out of range for type uInt32", e.getCause().getCause().getMessage());
        }
    }
}
