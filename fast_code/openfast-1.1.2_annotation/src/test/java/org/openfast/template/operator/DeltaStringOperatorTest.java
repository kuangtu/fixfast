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
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.TwinValue;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class DeltaStringOperatorTest extends OpenFastTestCase {
    private Scalar field;

    public void testDecodeSubtractionLengthError() {
        field = new Scalar("", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false);
        try {
            decode(twin(i(5), string("abc")), string("def"));
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D7_SUBTRCTN_LEN_LONG, e.getCode());
            assertEquals(
                    "The string diff <5, abc> cannot be applied to the base value \"def\" because the subtraction length is too long.",
                    e.getMessage());
        }
    }

    public void testGetValueToEncodeMandatory() {
        field = new Scalar("", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false);
        assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
        assertEquals(tv(1, "E"), encode("ABCE", string("ABCD")));
        assertEquals(tv(-2, "Z"), encode("ZBCE", string("ABCE")));
        assertEquals(tv(-1, "Y"), encode("YZBCE", string("ZBCE")));
        assertEquals(tv(0, "F"), encode("YZBCEF", string("YZBCE")));
    }

    public void testDecodeValueMandatory() {
        field = new Scalar("", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false);
        assertEquals(string("ABCD"), decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
        assertEquals(string("ABCE"), decode(tv(1, "E"), string("ABCD")));
        assertEquals(string("ZBCE"), decode(tv(-2, "Z"), string("ABCE")));
        assertEquals(string("YZBCE"), decode(tv(-1, "Y"), string("ZBCE")));
        assertEquals(string("YZBCEF"), decode(tv(0, "F"), string("YZBCE")));
    }

    private ByteVectorValue str2bv(String string) {
        return new ByteVectorValue(string.getBytes());
    }

    public void testGetValueToEncodeOptional() {
        field = new Scalar("", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, true);
        assertEquals(tv(0, "ABCD"), encode("ABCD", ScalarValue.UNDEFINED));
        assertEquals(tv(1, "E"), encode("ABCE", string("ABCD")));
        assertEquals(tv(-2, "Z"), encode("ZBCE", string("ABCE")));
        assertEquals(tv(-1, "Y"), encode("YZBCE", string("ZBCE")));
        assertEquals(tv(0, "F"), encode("YZBCEF", string("YZBCE")));
        assertEquals(ScalarValue.NULL, encode(null, string("YZBCEF")));
    }

    public void testDecodeValueOptional() {
        field = new Scalar("", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, true);
        assertEquals(new StringValue("ABCD"), decode(tv(0, "ABCD"), ScalarValue.UNDEFINED));
        assertEquals(new StringValue("ABCE"), decode(tv(1, "E"), string("ABCD")));
        assertEquals(new StringValue("ZBCE"), decode(tv(-2, "Z"), string("ABCE")));
        assertEquals(new StringValue("YZBCE"), decode(tv(-1, "Y"), string("ZBCE")));
        assertEquals(new StringValue("YZBCEF"), decode(tv(0, "F"), string("YZBCE")));
        assertEquals(null, decode(ScalarValue.NULL, string("YZBCEF")));
    }

    private ScalarValue encode(String value, ScalarValue priorValue) {
        if (value == null) {
            return OperatorCodec.DELTA_STRING.getValueToEncode(null, priorValue, field);
        }
        return OperatorCodec.DELTA_STRING.getValueToEncode(new StringValue(value), priorValue, field);
    }

    private ScalarValue decode(ScalarValue diff, ScalarValue priorValue) {
        return OperatorCodec.DELTA_STRING.decodeValue(diff, priorValue, field);
    }

    private TwinValue tv(int subtraction, String diff) {
        return new TwinValue(new IntegerValue(subtraction), str2bv(diff));
    }
}
