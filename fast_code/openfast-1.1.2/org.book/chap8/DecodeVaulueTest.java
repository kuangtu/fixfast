package chap8;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.ByteUtil;

import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;

import junit.framework.TestCase;

public class DecodeVaulueTest extends TestCase {

	public void testNoneMand() {
		OperatorCodec none = Operator.NONE.getCodec(Type.U32);
		Scalar field = new Scalar("none", Type.U32, Operator.NONE, new IntegerValue(4), false);
		assertEquals(new IntegerValue(1), none.decodeValue(new IntegerValue(1), new IntegerValue(3), field));
	}

	public void testNoneOptNULL() {
		OperatorCodec none = Operator.NONE.getCodec(Type.U32);
		Scalar field = new Scalar("none", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, true);
		assertEquals(ScalarValue.NULL, none.decodeValue(ScalarValue.NULL, new IntegerValue(3), field));
	}

	public void testConstantOptnull() {
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);

		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(4), true);

		assertEquals(null, constant.decodeEmptyValue(new IntegerValue(3), field));
	}

	public void testConstantMandPrest() {
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(4), false);
		assertEquals(new IntegerValue(4), constant.decodeValue(new IntegerValue(5), new IntegerValue(3), field));
	}

	public void testDefaultManPrest() {
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(4), false);
		assertEquals(new IntegerValue(2), def.decodeValue(new IntegerValue(2), new IntegerValue(3), field));
	}

	public void testDefaultOptionalNULL() {
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		Scalar field = new Scalar("", Type.U32, Operator.DEFAULT, new IntegerValue(4), true);
		assertEquals(ScalarValue.NULL, def.decodeValue(ScalarValue.NULL, new IntegerValue(2), field));
	}

	public void testDefaultOptNotPrest() {
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(4), true);
		assertEquals(new IntegerValue(4), def.decodeEmptyValue(new IntegerValue(3), field));
	}

	public void testDefaultOptionalNotPrestNoInit() {
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, ScalarValue.UNDEFINED, true);
		assertEquals(null, def.decodeEmptyValue(new IntegerValue(2), field));
	}

	public void testCopyManPrest() {
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(4), false);
		assertEquals(new IntegerValue(2), copy.decodeValue(new IntegerValue(2), new IntegerValue(3), field));
	}

	public void testCopyOptNotPrest() {
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(4), true);
		assertEquals(new IntegerValue(2), copy.decodeEmptyValue(new IntegerValue(2), field));
	}

	public void testCopyOptNotPrestUndef() {
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(4), true);
		assertEquals(new IntegerValue(4), copy.decodeEmptyValue(ScalarValue.UNDEFINED, field));
	}

	public void testCopyDOptNotPrestNoInit() {
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		assertEquals(null, copy.decodeEmptyValue(ScalarValue.UNDEFINED, field));
	}

	public void testIncrMandPrest() {
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, new IntegerValue(4), false);
		assertEquals(new IntegerValue(2), incr.decodeValue(new IntegerValue(2), new IntegerValue(3), field));
	}

	public void testIncrOptiNotPrest() {
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, new IntegerValue(4), true);
		assertEquals(new IntegerValue(5), incr.decodeEmptyValue(new IntegerValue(4), field));
	}

	public void testIncrOptNotPrestInit() {
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, new IntegerValue(4), true);
		assertEquals(new IntegerValue(4), incr.decodeEmptyValue(ScalarValue.UNDEFINED, field));
	}

	public void testIncrOptionalNotPrestNoInit() {
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, true);
		assertEquals(null, incr.decodeEmptyValue(ScalarValue.UNDEFINED, field));
	}

	public void testIntDeltaMand() {
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(4), false);
		assertEquals(new IntegerValue(0), delta.decodeValue(new IntegerValue(4), new IntegerValue(-4), field));
	}

	public void testDeltaMandUndef() {
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(4), false);
		assertEquals(new IntegerValue(8), delta.decodeValue(new IntegerValue(4), ScalarValue.UNDEFINED, field));
	}

	public void testDeltaOptNULL() {
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(4), true);
		assertEquals(null, delta.decodeValue(ScalarValue.NULL, ScalarValue.UNDEFINED, field));
	}

	public void testDeltaOptUndefNoInit() {
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);

		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, true);

		assertEquals(new IntegerValue(4), delta.decodeValue(new IntegerValue(4), ScalarValue.UNDEFINED, field));
	}

	public void testDecimalDeltaMand() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA, new DecimalValue(10.0), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		DecimalValue value = (DecimalValue) delta.decodeValue(new DecimalValue(1.0), new DecimalValue(2.2), field);
		assertEquals(23, value.mantissa);
		assertEquals(-1, value.exponent);
	}

	public void testDecimalDeltaOptUndef() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA, new DecimalValue(10.0), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		DecimalValue value = (DecimalValue) delta.decodeValue(new DecimalValue(1.0), ScalarValue.UNDEFINED, field);
		assertEquals(2, value.mantissa);
		assertEquals(1, value.exponent);
	}

	public void testDecimalDeltaOptUndefNoInit() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA, ScalarValue.UNDEFINED, true);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		DecimalValue value = (DecimalValue) delta.decodeValue(new DecimalValue(10.9), ScalarValue.UNDEFINED, field);

		assertEquals(109, value.mantissa);
		assertEquals(-1, value.exponent);
	}

	public void testStrDeltaMand() {
		Scalar field = new Scalar("", Type.ASCII, Operator.DELTA, new StringValue("abc"), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		ScalarValue value = delta.decodeValue(BookUtilTest.tv(1, "ba"), new StringValue("abc"), field);
		assertEquals(value.toString(), "abba");
	}

	public void testStrDeltaOptUndef() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA, new StringValue("abc"), true);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		ScalarValue value = delta.decodeValue(BookUtilTest.tv(-2, "aaa"), ScalarValue.UNDEFINED, field);
		assertEquals(value.toString(), "aaabc");
	}

}