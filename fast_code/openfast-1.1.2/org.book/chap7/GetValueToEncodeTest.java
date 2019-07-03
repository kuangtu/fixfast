package chap7;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.ByteUtil;

import junit.framework.TestCase;

public class GetValueToEncodeTest extends TestCase {

	public void testNoneEncodeMand() {
		Scalar field = new Scalar("none", Type.U32, Operator.NONE, ScalarValue.UNDEFINED, false);

		OperatorCodec none = Operator.NONE.getCodec(Type.U32);

		assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field));

		assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), ScalarValue.NULL, field));

		assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), ScalarValue.UNDEFINED, field));
	}

	public void testNoneOptNULL() {
		Scalar field = new Scalar("none", Type.U32, Operator.NONE, new IntegerValue(1), true);
		OperatorCodec none = Operator.NONE.getCodec(Type.U32);
		assertEquals(ScalarValue.NULL, none.getValueToEncode(null, new IntegerValue(1), field));
	}

	public void testConstantMand() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(1), false);
		BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(1);
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
		assertEquals(null,
				constant.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field, presenceMapBuilder));
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));

	}

	public void testConstantOpt() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(1), true);
		BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
		assertEquals(null,
				constant.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field, presenceMapBuilder));
		assertEquals("11000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));
		assertEquals(1, presenceMapBuilder.getIndex());
	}

	public void testConstantOptNULL() {
		Scalar field = new Scalar("constant", Type.U32, Operator.CONSTANT, new IntegerValue(1), true);
		BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
		assertEquals(null, constant.getValueToEncode(null, new IntegerValue(2), field, presenceMapBuilder));
		assertEquals("10000000", ByteUtil.convertByteArrayToBitString(presenceMapBuilder.getBitVector().getBytes()));
		assertEquals(1, presenceMapBuilder.getIndex());
	}

	public void testDefaultMand() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(1), false);
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		assertEquals(new IntegerValue(4), def.getValueToEncode(new IntegerValue(4), new IntegerValue(2), field));
	}

	public void testDefaultMandAbsent() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(1), false);
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		assertEquals(null, def.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field));
	}

	public void testDefaultOptNULL() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, new IntegerValue(1), true);
		BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		assertEquals(ScalarValue.NULL, def.getValueToEncode(null, new IntegerValue(2), field, presenceMapBuilder));
	}

	public void testDefaultOptnull() {
		Scalar field = new Scalar("default", Type.U32, Operator.DEFAULT, ScalarValue.UNDEFINED, true);
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);
		assertEquals(null, def.getValueToEncode(null, new IntegerValue(1), field));
	}

	public void testCopyMand() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(10), false);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);

		assertEquals(new IntegerValue(1), copy.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field));
	}

	public void testCopyMandAbst() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(10), false);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		assertEquals(null, copy.getValueToEncode(new IntegerValue(10), ScalarValue.UNDEFINED, field));
	}

	public void testCopyOpt() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, new IntegerValue(10), true);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		assertEquals(null, copy.getValueToEncode(new IntegerValue(1), new IntegerValue(1), field));
	}

	public void testCopyOptNULLNoInit() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		BitVectorBuilder presenceMapBuilder = new BitVectorBuilder(7);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		assertEquals(null, copy.getValueToEncode(null, ScalarValue.UNDEFINED, field));
	}

	public void testCopyOptNULLAbst() {
		Scalar field = new Scalar("copy", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);
		OperatorCodec copy = Operator.COPY.getCodec(Type.U32);
		assertEquals(ScalarValue.NULL, copy.getValueToEncode(null, new IntegerValue(1), field));
	}
	
	public void testIncrMand()
	{
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, new IntegerValue(1), false);
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		assertEquals(new IntegerValue(4), incr.getValueToEncode(new IntegerValue(4), 
				new IntegerValue(2), field));
	}
	
	public void testIncrOptAbst()
	{
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT, new IntegerValue(1), true);
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		assertEquals(null, incr.getValueToEncode(new IntegerValue(3), 
				new IntegerValue(2), field));
	}
	
	public void testIncrOptUndef() {
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT,
				new IntegerValue(1), true);
		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		assertEquals(ScalarValue.NULL,
				incr.getValueToEncode(null, ScalarValue.UNDEFINED, field));
	} 

	public void testIncrOptUndef2() {
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT,
				ScalarValue.UNDEFINED, true);

		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);

		assertEquals(new IntegerValue(1), incr.getValueToEncode(
				new IntegerValue(1), ScalarValue.UNDEFINED, field));

	}

	public void testIncrOptUndef3() {
		Scalar field = new Scalar("incr", Type.U32, Operator.INCREMENT,
				ScalarValue.UNDEFINED, true);

		OperatorCodec incr = Operator.INCREMENT.getCodec(Type.U32);
		assertEquals(null,
				incr.getValueToEncode(null, ScalarValue.UNDEFINED, field));
	} 

	public void testIntDeltaMand()
	{
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA, new IntegerValue(1), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		assertEquals(new IntegerValue(2), delta.getValueToEncode(new IntegerValue(6), new IntegerValue(4), field));
	}

	public void testDeltaMandUnDef() {
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA,
				new IntegerValue(1), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		assertEquals(new IntegerValue(1), delta.getValueToEncode(
				new IntegerValue(2), ScalarValue.UNDEFINED, field));
	}

	public void testDeltaMandUndefNoInit() {
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA,
				ScalarValue.UNDEFINED, false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		assertEquals(new IntegerValue(2), delta.getValueToEncode(
				new IntegerValue(2), ScalarValue.UNDEFINED, field));
	}

	public void testDeltaOptNULL() {
		Scalar field = new Scalar("delta", Type.U32, Operator.DELTA,
				new IntegerValue(1), true);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.U32);
		assertEquals(ScalarValue.NULL,
				delta.getValueToEncode(null, new IntegerValue(2), field));
	}

	public void testDecDeltaMand() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA,
				new DecimalValue(2014.1024), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		assertEquals(new DecimalValue(18127.1), delta.getValueToEncode(
				new DecimalValue(2014.12), new DecimalValue(2014.10), field));
	}

	
	public void testDecDeltaManUndef() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA,
				new DecimalValue(2014.3), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		assertEquals(new DecimalValue(18126.9), delta.getValueToEncode(
				new DecimalValue(2014.12), ScalarValue.UNDEFINED, field));
	}

	public void testDecDeltaMandUndefNoInit() {
		Scalar field = new Scalar("delta", Type.DECIMAL, Operator.DELTA,
				ScalarValue.UNDEFINED, false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.DECIMAL);
		assertEquals(new DecimalValue(2014.12), delta.getValueToEncode(
				new DecimalValue(2014.12), ScalarValue.UNDEFINED, field));
	}

	public void testAsciiDeltaMand() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA,
				new StringValue("abc"), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		IntegerValue encodeInteger = new IntegerValue(-1);
		ByteVectorValue str = new ByteVectorValue("ab".getBytes());
		assertEquals(new TwinValue(encodeInteger, str), delta.getValueToEncode(
				new StringValue("abcd"), new StringValue("cd"), field));
	}

	public void testAsciiDeltaMandUndef() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA,
				new StringValue("abc"), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		IntegerValue encodeInteger = new IntegerValue(0);
		ByteVectorValue str = new ByteVectorValue("d".getBytes());
		assertEquals(new TwinValue(encodeInteger, str), delta.getValueToEncode(
				new StringValue("abcd"), ScalarValue.UNDEFINED, field));
	}

	
	public void testAsciiDeltaMandRemoveAll() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA,
				new StringValue("abc"), false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		IntegerValue encodeInteger = new IntegerValue(4);
		ByteVectorValue encodeString = new ByteVectorValue("abcd".getBytes());
		assertEquals(new TwinValue(encodeInteger, encodeString),
				delta.getValueToEncode(new StringValue("abcd"),
						new StringValue("efgh"), field));
	}
	
	
	public void testAsciiDeltaMandUndefNoInit() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA,
				ScalarValue.UNDEFINED, false);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		IntegerValue encodeInteger = new IntegerValue(0);
		ByteVectorValue str = new ByteVectorValue("abcd".getBytes());
		assertEquals(new TwinValue(encodeInteger, str), delta.getValueToEncode(
				new StringValue("abcd"), ScalarValue.UNDEFINED, field));
	}

	public void testAsciiDeltaOptNULL() {
		Scalar field = new Scalar("delta", Type.ASCII, Operator.DELTA,
				ScalarValue.UNDEFINED, true);
		OperatorCodec delta = Operator.DELTA.getCodec(Type.ASCII);
		assertEquals(ScalarValue.NULL,
				delta.getValueToEncode(null, ScalarValue.UNDEFINED, field));
	}


	


	
}