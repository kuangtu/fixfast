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

import java.math.BigDecimal;

import org.openfast.DecimalValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;


public class DeltaDecimalOperatorTest extends OpenFastTestCase {
    public void testGetValueToEncodeForMandatory() {
        Scalar field = new Scalar("", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false);
        OperatorCodec operator = field.getOperatorCodec();

        DecimalValue value = (DecimalValue) operator.getValueToEncode(d(9427.55),ScalarValue.UNDEFINED, field);
        assertEquals(BigDecimal.valueOf(9427.55), value.toBigDecimal());

        value = (DecimalValue) operator.getValueToEncode(d(9427.51), d(9427.55), field);
        assertEquals(-4, value.mantissa);
        assertEquals(0, value.exponent);

        value = (DecimalValue) operator.getValueToEncode(d(9427.46), d(9427.51), field);
        assertEquals(-5, value.mantissa);
        assertEquals(0, value.exponent);

        value = (DecimalValue) operator.getValueToEncode(d(30.6), d(30.6), field);
        assertEquals(0, value.exponent);
        assertEquals(0, value.mantissa);
    }

    public void testGetValueToEncodeForOptional() {
        Scalar field = new Scalar("", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, true);
        OperatorCodec operator = field.getOperatorCodec();

        DecimalValue value = (DecimalValue) operator.getValueToEncode(d(9427.55),
                ScalarValue.UNDEFINED, field);
        assertEquals(BigDecimal.valueOf(9427.55), value.toBigDecimal());

        value = (DecimalValue) operator.getValueToEncode(d(9427.51),
                d(9427.55), field);
        assertEquals(-4, value.mantissa);
        assertEquals(0, value.exponent);

        value = (DecimalValue) operator.getValueToEncode(d(9427.46),
                d(9427.51), field);
        assertEquals(-5, value.mantissa);
        assertEquals(0, value.exponent);

        value = (DecimalValue) operator.getValueToEncode(d(30.6), d(30.6), field);
        assertEquals(0, value.exponent);
        assertEquals(0, value.mantissa);

        assertEquals(ScalarValue.NULL,
            operator.getValueToEncode(null, d(30.6), field));
    }

    public void testGetValueToEncodeForMandatoryFieldAndDefaultValue() {
        Scalar field = new Scalar("", Type.DECIMAL, Operator.DELTA, d(12000),
                false);
        DecimalValue value = (DecimalValue) field.getOperatorCodec()
                                                 .getValueToEncode(d(12000),
                ScalarValue.UNDEFINED, field);
        assertEquals(0, value.mantissa);
        assertEquals(0, value.exponent);

        value = (DecimalValue) field.getOperatorCodec()
                                    .getValueToEncode(d(12100), d(12000), field);
        assertEquals(109, value.mantissa);
        assertEquals(-1, value.exponent);

        value = (DecimalValue) field.getOperatorCodec()
                                    .getValueToEncode(d(12150), d(12100), field);
        assertEquals(1094, value.mantissa);
        assertEquals(-1, value.exponent);

        value = (DecimalValue) field.getOperatorCodec()
                                    .getValueToEncode(d(12200), d(12150), field);
        assertEquals(-1093, value.mantissa);
        assertEquals(1, value.exponent);
    }

    public void testDecodeForMandatoryFieldAndDefaultValue() {
        Scalar field = new Scalar("", Type.DECIMAL, Operator.DELTA, d(12000),
                false);
        assertEquals(d(12000),
            OperatorCodec.DELTA_DECIMAL.decodeEmptyValue(ScalarValue.UNDEFINED, field));
        assertEquals(d(12100),
            OperatorCodec.DELTA_DECIMAL.decodeValue(d(109, -1), d(12000), field));
        assertEquals(d(12150),
            OperatorCodec.DELTA_DECIMAL.decodeValue(d(1094, -1), d(12100), field));
        assertEquals(d(12200),
            OperatorCodec.DELTA_DECIMAL.decodeValue(d(-1093, 1), d(12150), field));
    }

    public void testEncodeDecimalValueWithEmptyPriorValue() {
        try {
            Scalar field = new Scalar("", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, false);
            field.getOperatorCodec()
                 .getValueToEncode(null, ScalarValue.UNDEFINED, field);
            fail();
        } catch (FastException e) {
        	assertEquals(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, e.getCode());
        }
    }
    
    public void testEncodeDecimalValueWithOptionalField() {
    	assertEncodeDecode(d(-37.0), "10000001 11011011", TypeCodec.NULLABLE_SF_SCALED_NUMBER);
    }
}
