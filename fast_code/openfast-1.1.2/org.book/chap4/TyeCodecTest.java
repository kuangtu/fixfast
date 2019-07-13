package chap4;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.DateValue;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.test.OpenFastTestCase;

public class TyeCodecTest extends OpenFastTestCase {

	public void testUnsignedIntegerCodec() {
		ScalarValue value = new IntegerValue(1000);
		byte[] encoding = TypeCodec.UINT.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000111 11101000", res);
	}

	public void testUnsignedIntegerPrefixCodec() {
		ScalarValue value = new IntegerValue(64);
		byte[] encoding = TypeCodec.INTEGER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000000 11000000", res);
	}

	public void testMinus64Integer() {
		ScalarValue value = null;
		String interger = "11000000"; // -64的二进制编码
		InputStream in = ByteUtil.createByteStream(interger);
		value = TypeCodec.INTEGER.decode(in);
		assertEquals(-64, value.toInt());

	}

	public void testSignedInterCodec() {
		ScalarValue value = new IntegerValue(-1024);
		byte[] encoding = TypeCodec.INTEGER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("01111000 10000000", res);
	}

	public void testAsciiStrCodec() {
		ScalarValue value = new StringValue("CME");
		byte[] encoding = TypeCodec.ASCII.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("01000011 01001101 11000101", res);
	}

	public void testAsciiEmptyStrCodec() {
		ScalarValue value = new StringValue("");
		byte[] encoding = TypeCodec.ASCII.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000000", res);

		value = new StringValue("\0");
		encoding = TypeCodec.ASCII.encode(value);
		res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00000000 10000000", res);
	}

	public void testVetorCodec() {
		ScalarValue value = new ByteVectorValue("abc".getBytes());
		byte[] encoding = TypeCodec.BYTE_VECTOR.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10000011 01100001 01100010 01100011", res);
	}

	public void testUnicodeStrCodec() {
		ScalarValue value = new StringValue("交易所");
		byte[] encoding = TypeCodec.UNICODE.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("10001001 11100100 10111010 10100100 11100110 10011000 " + "10010011 11100110 10001001 10000000",
				res);
	}

	public void testDecimalCodec() {
		ScalarValue value = new DecimalValue(1204.01);
		byte[] encoding = TypeCodec.SF_SCALED_NUMBER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("11111110 00000111 00101100 11010001", res);
	}

}
