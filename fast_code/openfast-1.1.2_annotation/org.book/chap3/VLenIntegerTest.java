package chap3;

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

public class VLenIntegerTest extends OpenFastTestCase {



	public void testVLenInteger() {
		ScalarValue value = new IntegerValue(1024);
		byte[] encoding = TypeCodec.INTEGER.encode(value);
		String res = ByteUtil.convertByteArrayToBitString(encoding);
		assertEquals("00001000 10000000", res);
	}


}
