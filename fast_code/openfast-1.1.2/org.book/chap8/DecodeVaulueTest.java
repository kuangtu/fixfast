package chap8;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.ByteUtil;

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

}