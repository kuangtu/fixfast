package chap16;

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

public class PrimitiveArchTest extends OpenFastTestCase {

	public void testIntegerEncode() {
		// 无符号整数127,二进制表示为:0111 1111,只需要一个字节,
		// 将最高有效位置为1,得到1111 1111
		IntegerValue value = new IntegerValue(127);
		byte[] res = TypeCodec.UINT.encode(value);
		assertEquals("11111111", res);

		// 如果字段值为空,null编码后为1000 0000
		ScalarValue a = ScalarValue.NULL;
		res = TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(a);
		assertEquals("10000000", res);

		// 如果字段值为0,编码后为1000 0001
		value = new IntegerValue(0);
		res = TypeCodec.NULLABLE_UNSIGNED_INTEGER.encode(value);
		assertEquals("10000001", res);

		// 如果字段值为0,编码后为1000 0000
		value = new IntegerValue(0);
		res = TypeCodec.UINT.encode(value);
		assertEquals("10000000", res);
	}

	public void testSignedIntegerEncode() {

		// 63二进制编码为: 0011 1111
		// 对于该字节最高有效位设置为1,表示停止位
		// 第二个bit位为0,表示整数
		IntegerValue value = new IntegerValue(63);
		int i = 63;
		byte[] res = TypeCodec.INTEGER.encode(value);
		assertEquals("10111111", res);

		// 64二进制编码为:01000 000,
		// 如果直接在字节最高为添加1,表示停止位
		// 则认为符号为是1,是负数
		// 因此需要增加前导(leading zeros)的0
		value = new IntegerValue(64);
		i = 64;
		res = TypeCodec.INTEGER.encode(value);
		assertEquals("00000000 11000000", res);

		// 如果是可空类型的整数,值非负,则压缩前将值加1
		res = TypeCodec.NULLABLE_INTEGER.encode((value));
		assertEquals("00000000 11000001", res);
	}

	public void testSignedIntegerDecode() {
		String bitString = "11000000";
		ScalarValue value = new IntegerValue(-64);
		assertEquals(value, TypeCodec.INTEGER.decode(ByteUtil.createByteStream(bitString)));
	}

	public void testAsciiEncode() {
		StringValue value = new StringValue("123");
		byte[] res = TypeCodec.ASCII.encode(value);
		// 字符串"123",转换为字节数组为,最后一个字节通过停止位进行编码
		assertEquals("00110001 00110010 10110011", res);

		// 非空(non nullable)字符串字符串类型,编码为:0x80
		value = new StringValue("");
		res = TypeCodec.ASCII.encode(value);
		assertEquals("10000000", res);
		// 可空(nullable)字符串字符串类型,编码为:0x00 0x80
		value = new StringValue("");
		res = TypeCodec.NULLABLE_ASCII.encode(value);
		assertEquals("00000000 10000000", res);

		// 对于开始为字符\0的字符串,均通过0x00 0x00表示。
		// 最后一个字节通过停止位进行编码
		value = new StringValue("\0");
		res = TypeCodec.ASCII.encode(value);
		assertEquals("00000000 10000000", res);

		value = new StringValue("\0\123");
		res = TypeCodec.ASCII.encode(value);
		assertEquals("00000000 10000000", res);

		value = new StringValue("CME");
		String bitString = "01000011 01001101 11000101";
		ScalarValue strRes = TypeCodec.ASCII.decode(bitStream(bitString));
		assertEquals(value, strRes);
	}

	public void testByteVectorEncode() {
		ByteVectorValue value = new ByteVectorValue(new byte[] { 0x00 });
		byte[] res = TypeCodec.BYTE_VECTOR.encode(value);
		// 字节向量长度为1,压缩之后增加了长度前导
		assertEquals("10000001 00000000", res);

		// 如果字段为null,通过长度为空表示,压缩后为0x80
		res = TypeCodec.NULLABLE_BYTE_VECTOR_TYPE.encode(ScalarValue.NULL);
		assertEquals("10000000", res);

		StringValue str = new StringValue("架构");
		res = TypeCodec.UNICODE.encode(str);
		// 对于字符串"架构","架"的UTF8编码为:E69EB6,"构"的UTF8编码为:E69E84
		// 一共需要6个字节,因此长度为6
		assertEquals("10000110 11100110 10011110 10110110 11100110 10011110 10000100", res);

		res = TypeCodec.NULLABLE_UNICODE.encode(str);
		// 对于字符串"架构","架"的UTF8编码为:E69EB6,"构"的UTF8编码为:E69E84
		// 一共需要6个字节,但是可空的无符号长度编码前加1,因此为7.
		assertEquals("10000111 11100110 10011110 10110110 11100110 10011110 10000100", res);
		// 字节向量长度为2,
		value = new ByteVectorValue(new byte[] { 0x57, 0x4e });
		assertEquals(value, TypeCodec.BYTE_VECTOR.decode(bitStream("10000010 01010111 01001110")));

	}

}