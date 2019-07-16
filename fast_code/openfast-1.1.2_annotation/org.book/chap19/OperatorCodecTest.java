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

	public void testNoneCodec() 
	{
	  Scalar field = new Scalar("", Type.U32, Operator.NONE,  new IntegerValue(10), true);
	  OperatorCodec none = Operator.NONE.getCodec(Type.U16);
	  //不基于前值进行优化,直接压缩字段值
	  assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), null, field));
	  assertEquals(new IntegerValue(1), none.getValueToEncode(new IntegerValue(1), new IntegerValue(2), field));
	  
	  //字段值为null,通过NULL值表示.
	  assertEquals(ScalarValue.NULL, none.getValueToEncode(null, new IntegerValue(1), field));
	}

	
	

}