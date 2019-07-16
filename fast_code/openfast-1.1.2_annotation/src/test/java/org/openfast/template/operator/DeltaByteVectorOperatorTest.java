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
package org.openfast.template.operator;

import org.openfast.ByteVectorValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class DeltaByteVectorOperatorTest extends OpenFastTestCase {
    private Scalar field;

    public void testDecodeSubtractionLengthError() {
        field = new Scalar("", Type.BYTE_VECTOR, Operator.DELTA, ScalarValue.UNDEFINED, false);
        try {
            decode(twin(i(5), byt(byt("c0afcd"))), byt(byt("123456")));
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D7_SUBTRCTN_LEN_LONG, e.getCode());
        }
    }

    public void testGetValueToEncodeMandatory() {
        field = new Scalar("", Type.BYTE_VECTOR, Operator.DELTA, ScalarValue.UNDEFINED, false);
        assertEquals(tv(0, byt("aabbccdd")), encode("aabbccdd", ScalarValue.UNDEFINED));
        assertEquals(tv(1, byt("ee")), encode("aabbccee", bv("aabbccdd")));
        assertEquals(tv(-2, byt("ff")), encode("ffbbccee", bv("aabbccee")));
        assertEquals(tv(-1, byt("11")), encode("11ffbbccee", bv("ffbbccee")));
        assertEquals(tv(0, byt("ff")), encode("11ffbbcceeff", bv("11ffbbccee")));
    }

    public void testDecodeValueMandatory() {
        field = new Scalar("", Type.BYTE_VECTOR, Operator.DELTA, ScalarValue.UNDEFINED, false);
        assertEquals(bv("aabbccdd"), decode(tv(0, byt("aabbccdd")), ScalarValue.UNDEFINED));
        assertEquals(bv("aabbccee"), decode(tv(1, byt("ee")), bv("aabbccdd")));
        assertEquals(bv("ffbbccee"), decode(tv(-2, byt("ff")), bv("aabbccee")));
        assertEquals(bv("11ffbbccee"), decode(tv(-1, byt("11")), bv("ffbbccee")));
        assertEquals(bv("11ffbbcceeff"), decode(tv(0, byt("ff")), bv("11ffbbccee")));
    }

    public void testGetValueToEncodeOptional() {
        field = new Scalar("", Type.BYTE_VECTOR, Operator.DELTA, ScalarValue.UNDEFINED, true);
        assertEquals(tv(0, byt("aabbccdd")), encode("aabbccdd", ScalarValue.UNDEFINED));
        assertEquals(tv(1, byt("ee")), encode("aabbccee", bv("aabbccdd")));
        assertEquals(tv(-2, byt("ff")), encode("ffbbccee", bv("aabbccee")));
        assertEquals(tv(-1, byt("11")), encode("11ffbbccee", bv("ffbbccee")));
        assertEquals(tv(0, byt("ff")), encode("11ffbbcceeff", bv("11ffbbccee")));
        assertEquals(ScalarValue.NULL, encode(null, bv("11ffbbcceeff")));
    }

    public void testDecodeValueOptional() {
        field = new Scalar("", Type.BYTE_VECTOR, Operator.DELTA, ScalarValue.UNDEFINED, true);
        assertEquals(bv("aabbccdd"), decode(tv(0, byt("aabbccdd")), ScalarValue.UNDEFINED));
        assertEquals(bv("aabbccee"), decode(tv(1, byt("ee")), bv("aabbccdd")));
        assertEquals(bv("ffbbccee"), decode(tv(-2, byt("ff")), bv("aabbccee")));
        assertEquals(bv("11ffbbccee"), decode(tv(-1, byt("11")), bv("ffbbccee")));
        assertEquals(bv("11ffbbcceeff"), decode(tv(0, byt("ff")), bv("11ffbbccee")));
        assertEquals(null, decode(ScalarValue.NULL, string("11ffbbccee")));
    }

    private ScalarValue encode(String value, ScalarValue priorValue) {
        if (value == null) {
            return OperatorCodec.DELTA_STRING.getValueToEncode(null, priorValue, field);
        }
        return OperatorCodec.DELTA_STRING.getValueToEncode(bv(value), priorValue, field);
    }

    private ScalarValue decode(ScalarValue diff, ScalarValue priorValue) {
        return OperatorCodec.DELTA_STRING.decodeValue(diff, priorValue, field);
    }

    private TwinValue tv(int subtraction, byte[] bytes) {
        return new TwinValue(new IntegerValue(subtraction), new ByteVectorValue(bytes));
    }
}
