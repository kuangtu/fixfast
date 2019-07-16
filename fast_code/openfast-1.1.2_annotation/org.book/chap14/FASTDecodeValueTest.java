package chap14;

import org.openfast.template.*;

import org.openfast.template.type.*;
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

public class FASTDecodeValueTest extends OpenFastTestCase {

//	public void testCopyValue() {
//		MessageTemplate template1 = new MessageTemplate("",
//				new Field[] { new Scalar("1", Type.U32, Operator.COPY, new IntegerValue(10), false), });
//		Context context = new Context();
//
//		context.registerTemplate(113, template1);
//		FastEncoder encoder = new FastEncoder(context);
//
//		Message msg1 = new Message(template1);
//		msg1.setInteger(1, 20);
//		byte[] encoding = encoder.encode(msg1);
//
//		
//		String res1 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res1);
//		msg1.setInteger(1, 20);
//		encoding = encoder.encode(msg1);
//		String res2 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res2);
//		
//		
//
//
//		InputStream in = ByteUtil.createByteStream(res1 + ' ' + res2);
//
//
//		FastDecoder decoder = new FastDecoder(context, in);
//
//		Message msg_t1 = decoder.readMessage();
//		
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//		
//		msg_t1 = decoder.readMessage();
//		
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//
//	}
	
//	public void testNoneValue()
//	{
//		MessageTemplate template1 = new MessageTemplate("",
//				new Field[] { new Scalar("1", Type.U32, Operator.NONE, new IntegerValue(10), false), });
//		Context context = new Context();
//
//		context.registerTemplate(113, template1);
//		FastEncoder encoder = new FastEncoder(context);
//
//		Message msg1 = new Message(template1);
//		msg1.setInteger(1, 20);
//		byte[] encoding = encoder.encode(msg1);
//
//		
//		String res1 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res1);
//		msg1.setInteger(1, 20);
//		encoding = encoder.encode(msg1);
//		String res2 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res2);
//		
//		
//
//
//		InputStream in = ByteUtil.createByteStream(res1 + ' ' + res2);
//
//
//		FastDecoder decoder = new FastDecoder(context, in);
//
//		Message msg_t1 = decoder.readMessage();
//		
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//		
//		msg_t1 = decoder.readMessage();
//		
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//	}
	
//	public void testConstantValue()
//	{
//		MessageTemplate template1 = new MessageTemplate("",
//				new Field[] { new Scalar("1", Type.U32, Operator.CONSTANT, new IntegerValue(10), true), });
//		Context context = new Context();
//
//		context.registerTemplate(113, template1);
//		FastEncoder encoder = new FastEncoder(context);
//
//		Message msg1 = new Message(template1);
//		msg1.setInteger(1, 10);
//		byte[] encoding = encoder.encode(msg1);
//
//		String res1 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res1);
//
//
//		InputStream in = ByteUtil.createByteStream(res1);
//
//		FastDecoder decoder = new FastDecoder(context, in);
//
//		Message msg_t1 = decoder.readMessage();
//
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//		
//		
//	}

//	public void testDefaultValue()
//	{
//		MessageTemplate template1 = new MessageTemplate("",
//				new Field[] { new Scalar("1", Type.U32, Operator.DEFAULT, new IntegerValue(10), false), });
//		Context context = new Context();
//
//		context.registerTemplate(113, template1);
//		FastEncoder encoder = new FastEncoder(context);
//
//		Message msg1 = new Message(template1);
//		msg1.setInteger(1, 20);
//		byte[] encoding = encoder.encode(msg1);
//
//		String res1 = ByteUtil.convertByteArrayToBitString(encoding);
//		System.out.println("the res is:" + res1);
//
//
//		InputStream in = ByteUtil.createByteStream(res1);
//
//		FastDecoder decoder = new FastDecoder(context, in);
//
//		Message msg_t1 = decoder.readMessage();
//
//		System.out.println("the msg integer is: " + msg_t1.getInt(1));
//		
//		
//	}
	
	public void testDeltaValue()
	{
		MessageTemplate template1 = new MessageTemplate("",
				new Field[] { new Scalar("1", Type.U32, Operator.DELTA, new IntegerValue(10), false), });
		Context context = new Context();

		context.registerTemplate(113, template1);
		FastEncoder encoder = new FastEncoder(context);

		Message msg1 = new Message(template1);
		msg1.setInteger(1, 20);
		byte[] encoding = encoder.encode(msg1);

		String res1 = ByteUtil.convertByteArrayToBitString(encoding);
		System.out.println("the res is:" + res1);


		InputStream in = ByteUtil.createByteStream(res1);

		context.reset();
		FastDecoder decoder = new FastDecoder(context, in);

		Message msg_t1 = decoder.readMessage();

		System.out.println("the msg integer is: " + msg_t1.getInt(1));
		
		
		
	}
	
	
}