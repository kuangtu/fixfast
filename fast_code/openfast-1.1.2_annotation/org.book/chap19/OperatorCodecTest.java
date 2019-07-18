package chap19;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.template.operator.*;

import java.io.InputStream;

import org.openfast.*;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.*;
import org.openfast.template.loader.*;
import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;
import org.openfast.test.*;

import junit.framework.TestCase;

public class OperatorCodecTest extends OpenFastTestCase {

	public void testNoneCodec() {
		Scalar field = new Scalar("", Type.U32, Operator.NONE, new IntegerValue(10), true);
		OperatorCodec none = Operator.NONE.getCodec(Type.U16);
		// 不基于前值进行优化,直接压缩字段值
		assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), null, field));
		assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field));
		// 字段值为null,通过NULL值表示.
		assertEquals(ScalarValue.NULL, none.getValueToEncode(null, new IntegerValue(1), field));
	}

	public void testConstantCodec() {
		Scalar field = new Scalar("", Type.U32, Operator.CONSTANT, new IntegerValue(10), true);
		OperatorCodec constant = Operator.CONSTANT.getCodec(Type.U32);
		BitVectorBuilder bitVector = new BitVectorBuilder(1);
		// 不进行传输,得到需要压缩的值为null
		assertEquals(null, constant.getValueToEncode(null, null, field, bitVector));
		// 字段值为null,不需要设置存在图中对应bit位的值为1
		BitVector vector = bitVector.getBitVector();
		// PMAP中bit值为0
		assertEquals(false, vector.isSet(0));

		// 字段值不为null,设置PMAP中对应bit的值为1
		bitVector = new BitVectorBuilder(1);
		vector = bitVector.getBitVector();
		assertEquals(null, constant.getValueToEncode(new IntegerValue(10), null, field, bitVector));
		assertEquals(true, vector.isSet(0));
	}

	public void testDefaultCodec() {
		OperatorCodec def = Operator.DEFAULT.getCodec(Type.U32);

		Scalar field = new Scalar("operatorName", Type.U32, Operator.DEFAULT, new IntegerValue(1), false);
		// 字段值为1,和初始值相同,不需要传输,因此需要编码的值为null
		assertEquals(null, field.getOperatorCodec().getValueToEncode(new IntegerValue(1), null, field));
		// 字段值为2,和初始值不同,需要传输
		assertEquals(new IntegerValue(2),
				field.getOperatorCodec().getValueToEncode(new IntegerValue(2), new IntegerValue(3), field));
		// 数据流中值为4,则字段的值为4
		assertEquals(new IntegerValue(4), def.decodeValue(new IntegerValue(4), null, field));
		// 字段值在数据流中不出现,默认值为1,得到字段的值为1
		assertEquals(new IntegerValue(1), def.decodeEmptyValue(ScalarValue.UNDEFINED, field));

	}

	public void testDefaultCodec1() {

		Scalar field2 = new Scalar("operatorName", Type.U32, Operator.DEFAULT, ScalarValue.UNDEFINED, true);

		// 字段值为null,出现类型为可选类型,且默认值未定义,因此需要编码的值为null
		assertEquals(null, field2.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field2));
		// 字段值为3,需要编码的值为3
		assertEquals(new IntegerValue(3),
				field2.getOperatorCodec().getValueToEncode(new IntegerValue(3), ScalarValue.UNDEFINED, field2));

	}

	public void testDefaultCodec2() {

		Scalar field3 = new Scalar("operatorName", Type.U32, Operator.DEFAULT, new IntegerValue(3), true);

		// 字段值为null,存在出行为可选类型,而初始值为3,因此通过NULL表示
		assertEquals(ScalarValue.NULL, field3.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field3));

	}

	public void testIncrementCodec() {
		Scalar field = new Scalar("", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, true);
		// 前值为null,默认值未定义,字段值为1,传输值为1
		assertEquals(new IntegerValue(1), field.getOperatorCodec().getValueToEncode(new IntegerValue(1), null, field));

		// 前值为null,字段值为null,传输值为null
		assertEquals(null, field.getOperatorCodec().getValueToEncode(null, null, field));

		// 前值未定义,字段值为null,字段初始值未定义,传输值为null
		assertEquals(null, field.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field));

		// 前值为3,字段值为4,等于前值+1,不需要进行传输
		assertEquals(null, field.getOperatorCodec().getValueToEncode(new IntegerValue(4), new IntegerValue(3), field));

	}

	public void testIncrementCodec1() {
		Scalar field = new Scalar("", Type.U32, Operator.INCREMENT, new IntegerValue(3), true);

		// 字段值为null,字段初始值值为3,前值未定义,通过NULL表示
		assertEquals(ScalarValue.NULL, field.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field));

		// 字段值为3与初始值相同,前值未定义,传输值为null
		assertEquals(null,
				field.getOperatorCodec().getValueToEncode(new IntegerValue(3), ScalarValue.UNDEFINED, field));

	}

	public void testCopyOperatorCodec1() {

		Scalar field = new Scalar("", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, true);

		// 字段值为null,前值未定义,没有初始值,字段不需要进行传输
		// 返回null
		assertEquals(null, field.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field));

		// 字段值为null,前值为1,传输值为NULL
		assertEquals(ScalarValue.NULL, field.getOperatorCodec().getValueToEncode(null, new IntegerValue(1), field));

	}

	public void testCopyOperatorCodec2() {

		Scalar field2 = new Scalar("", Type.U32, Operator.COPY, new IntegerValue(1), true);

		// 字段值为null,前值未定义,初始值为1,传输值为NULL
		assertEquals(ScalarValue.NULL, field2.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field2));

	}

	public void testDeltaValueCodec1() {
		Scalar field = new Scalar("", Type.I32, Operator.DELTA, ScalarValue.UNDEFINED, true);
		// 字段值和前值相差了15
		assertEquals(new IntegerValue(15),
				field.getOperatorCodec().getValueToEncode(new IntegerValue(45), new IntegerValue(30), field));
		// 前值未定义,字段无初始值,则前值为整数基值0,得到差值为45
		assertEquals(new IntegerValue(45),
				field.getOperatorCodec().getValueToEncode(new IntegerValue(45), ScalarValue.UNDEFINED, field));

	}

	public void testDeltaValueCodec2() {
		Scalar field = new Scalar("", Type.I32, Operator.DELTA, new IntegerValue(25), true);
		// 前值未定义,取字段初始值25,则前值为25,字段值为30,差值为5
		assertEquals(new IntegerValue(5),
				field.getOperatorCodec().getValueToEncode(new IntegerValue(30), ScalarValue.UNDEFINED, field));
		// 前值未定义，取字段初始值，通过NULL表示
		assertEquals(ScalarValue.NULL, field.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field));
		// 前值为10，字段值为null，通过NULL表示
		assertEquals(ScalarValue.NULL, field.getOperatorCodec().getValueToEncode(null, new IntegerValue(10), field));
	}
	
	
	public void testTailValueCode() {
		Scalar field = new Scalar("", Type.ASCII, Operator.TAIL, new StringValue("abce"), true);
		
		// 前值未定义，取字段初始值abce，字段值为abcd，结果为d
		assertEquals(new StringValue("d"), field.getOperatorCodec().getValueToEncode(new StringValue("abcd"), ScalarValue.UNDEFINED, field));
				
		//前值未定义，字段值为null,通过NULL表示
		assertEquals(ScalarValue.NULL, field.getOperatorCodec().getValueToEncode(null, ScalarValue.UNDEFINED, field));
	}
	

}