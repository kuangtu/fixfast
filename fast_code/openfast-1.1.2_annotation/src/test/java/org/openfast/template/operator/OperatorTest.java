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

import junit.framework.TestCase;

import org.openfast.BitVectorBuilder;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

public class OperatorTest extends TestCase {

	public void testDefaultOperator()
	{
		Scalar field = new Scalar("operatorName", Type.U32, Operator.DEFAULT, new IntegerValue(1), false);
		assertEquals(null, field.getOperatorCodec().getValueToEncode(new IntegerValue(1), null, field));
//		newly added implementation
		assertEquals(new IntegerValue(2), field.getOperatorCodec().getValueToEncode(new IntegerValue(2), null, field));
	}
	
	public void testCopyOperator()
	{
		Scalar field = new Scalar("", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		assertEquals(new IntegerValue(1), copy.getValueToEncode(new IntegerValue(1), null, field));
		assertEquals(new IntegerValue(2), copy.getValueToEncode(new IntegerValue(2), new IntegerValue(1), field));
		//newly added implementation
		assertEquals(null, copy.getValueToEncode(ScalarValue.NULL, ScalarValue.NULL, field));
	}
	
	public void testCopyOperatorWithOptionalPresence()
	{
		OperatorCodec copy = OperatorCodec.COPY_ALL;
		Scalar field = new Scalar("", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		assertEquals(null, copy.getValueToEncode(null, ScalarValue.UNDEFINED, field));
		//newly added implementation	
		Scalar field1 = new Scalar("", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		assertEquals(null, copy.decodeEmptyValue(ScalarValue.UNDEFINED, field1));
	}
	
	public void testIncrementOperatorWithNoDefaultValue()
	{
		Scalar field = new Scalar("", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, false);
		assertEquals(new IntegerValue(1), OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(1), null, field));
		assertEquals(null, OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(2), new IntegerValue(1), field));
	}
	
	public void testIncrementOperatorWithDefaultValue()
	{
		Scalar field = new Scalar("", Type.U32, Operator.INCREMENT, new IntegerValue(1), false);
		assertEquals(null, OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(1), ScalarValue.UNDEFINED, field));
		assertEquals(null, OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(2), new IntegerValue(1), field));
		assertEquals(new IntegerValue(3), OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(3), new IntegerValue(1), field));
		assertEquals(new IntegerValue(3), OperatorCodec.INCREMENT_INTEGER.getValueToEncode(new IntegerValue(3), null, field));
	}
	
	public void testConstantValueOperator()
	{
		Scalar field = new Scalar("", Type.ASCII, Operator.CONSTANT, new StringValue("5"), false);
		assertEquals(null, OperatorCodec.CONSTANT_ALL.getValueToEncode(null, null, field, new BitVectorBuilder(1)));	
		Scalar field1 = new Scalar("", Type.ASCII, Operator.CONSTANT, new StringValue("99"), false);
		assertEquals(null, OperatorCodec.CONSTANT_ALL.getValueToEncode(null, null, field1, new BitVectorBuilder(1)));
		//newly added implementation
		Scalar field2 = new Scalar("", Type.ASCII, Operator.CONSTANT, new StringValue("4"), true);
		assertEquals(null, OperatorCodec.CONSTANT_ALL.decodeEmptyValue(new StringValue("4"), field2));
	}
	
	public void testDeltaValueOperatorForEncodingIntegerValue()
	{
		Scalar field = new Scalar("", Type.I32, Operator.DELTA, ScalarValue.UNDEFINED, false);
		assertEquals(new IntegerValue(15), field.getOperatorCodec().getValueToEncode(new IntegerValue(45), new IntegerValue(30), field));
		assertEquals(new IntegerValue(-15), field.getOperatorCodec().getValueToEncode(new IntegerValue(30), new IntegerValue(45), field));
		field = new Scalar("", Type.I32, Operator.DELTA, new IntegerValue(25), false);
		assertEquals(new IntegerValue(5), field.getOperatorCodec().getValueToEncode(new IntegerValue(30), ScalarValue.UNDEFINED, field));
	}
	
	public void testDeltaValueOperatorForDecodingIntegerValue()
	{
		assertEquals(new IntegerValue(45), OperatorCodec.DELTA_INTEGER.decodeValue(new IntegerValue(15), new IntegerValue(30), null));
		assertEquals(new IntegerValue(30), OperatorCodec.DELTA_INTEGER.decodeValue(new IntegerValue(-15), new IntegerValue(45), null));
		Scalar field = new Scalar("", Type.I32, Operator.DELTA, new IntegerValue(25), false);
		assertEquals(new IntegerValue(30), OperatorCodec.DELTA_INTEGER.decodeValue(new IntegerValue(5), ScalarValue.UNDEFINED, field));
		Scalar field2 = new Scalar("", Type.I32, Operator.DELTA, new IntegerValue(25), false);
		assertEquals(new IntegerValue(25), OperatorCodec.DELTA_INTEGER.decodeEmptyValue(ScalarValue.UNDEFINED, field2));
		assertEquals(new IntegerValue(5), OperatorCodec.DELTA_INTEGER.decodeEmptyValue(new IntegerValue(5), field));
		Scalar field1 = new Scalar("", Type.I32, Operator.DELTA, ScalarValue.UNDEFINED, true);
		assertEquals(ScalarValue.UNDEFINED, OperatorCodec.DELTA_INTEGER.decodeEmptyValue(ScalarValue.UNDEFINED, field1));
	}
	
	public void testDeltaValueOperatorForEncodingIntegerValueWithEmptyPriorValue()
	{
		try {
			Scalar field = new Scalar("", Type.I32, Operator.DELTA, new IntegerValue(25), false);
			field.getOperatorCodec().getValueToEncode(new IntegerValue(30), null, field);
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, e.getCode());
		}
	}
	
	public void testDeltaValueOperatorForDecodingIntegerValueWithEmptyPriorValue()
	{
		try {
			Scalar field = new Scalar("", Type.U32, Operator.DELTA, new IntegerValue(25), false);
			OperatorCodec.DELTA_INTEGER.decodeValue(new IntegerValue(30), null, field);
			//newly added implementation
			Scalar field1 = new Scalar("", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, false);
			assertEquals(ScalarValue.UNDEFINED,OperatorCodec.DELTA_INTEGER.decodeEmptyValue(ScalarValue.UNDEFINED, field1));
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, e.getCode());
		}
	}
	
	public void testDeltaOperatorForOptionalUnsignedInteger() {
		Scalar field = new Scalar("", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, true);
		OperatorCodec delta = field.getOperatorCodec();
		assertEquals(ScalarValue.NULL, delta.getValueToEncode(null, ScalarValue.UNDEFINED, field));
	}
	
	public void testIncompatibleOperatorAndTypeError() {
		try {
			Operator.INCREMENT.getCodec(Type.STRING);
			fail();
		} catch (FastException e) {
			assertEquals(FastConstants.S2_OPERATOR_TYPE_INCOMP, e.getCode());
			assertEquals("The operator \"increment\" is not compatible with type \"string\"", e.getMessage());
		}
	}
}
