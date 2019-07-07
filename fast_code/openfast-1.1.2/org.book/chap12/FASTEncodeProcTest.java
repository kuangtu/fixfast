package chap12;

import org.openfast.template.*;

import org.openfast.template.type.*;
import org.openfast.template.operator.*;
import org.openfast.*;
import org.openfast.codec.FastEncoder;
import org.openfast.template.*;
import org.openfast.template.loader.*;
import bookUtil.BookUtilTest;
import bookUtil.BookUtilTest.*;
import org.openfast.test.*;

import junit.framework.TestCase;

public class FASTEncodeProcTest extends OpenFastTestCase {

	public void testEncodeTemplateID() {
		MessageTemplate template1 = new MessageTemplate("",
				new Field[] { new Scalar("1", Type.I32, Operator.COPY,
						ScalarValue.UNDEFINED, true) });
		MessageTemplate template2 = new MessageTemplate("",
				new Field[] { new Scalar("2", Type.I32, Operator.DEFAULT,
						ScalarValue.UNDEFINED, true) });
		Context context = new Context();
		//注册模板id
		context.registerTemplate(10, template1);
		context.registerTemplate(11, template2);
		//生成对应模板的消息
		Message message1 = new Message(template1);
		Message message2 = new Message(template2);
		byte[] encoding = new FastEncoder(context).encode(message1);
		assertEquals("11000000 10001010", encoding);
		encoding = new FastEncoder(context).encode(message2);
		assertEquals("11000000 10001011", encoding);
		encoding = new FastEncoder(context).encode(message2);
		assertEquals("10000000", encoding);
	}

	
	public void testEncodeEmptyMessage() {
		MessageTemplate template1 = new MessageTemplate("",
				new Field[] { new Scalar("1", Type.I32, Operator.COPY,
						ScalarValue.UNDEFINED, true) });
		MessageTemplate template2 = new MessageTemplate("",
				new Field[] { new Scalar("2", Type.I32, Operator.DEFAULT,
						new IntegerValue(1), true) });
		Context context = new Context();
		//注册模板id
		context.registerTemplate(10, template1);
		context.registerTemplate(11, template2);
		//生成对应模板的消息
		Message message1 = new Message(template1);
		Message message2 = new Message(template2);
		byte[] encoding = new FastEncoder(context).encode(message1);
		assertEquals("11000000 10001010", encoding);
		encoding = new FastEncoder(context).encode(message2);
		assertEquals("11100000 10001011 10000000", encoding);
		encoding = new FastEncoder(context).encode(message2);
		assertEquals("10100000 10000000", encoding);
		encoding = new FastEncoder(context).encode(message1);
		assertEquals("11000000 10001010", encoding);
	}

	   public void testEncodeMessageWithSignedIntegerFieldTypesAndAllOperators() {
	        MessageTemplate template = new MessageTemplate("",
	                new Field[] {
	                    new Scalar("1", Type.I32, Operator.COPY, ScalarValue.UNDEFINED, false),
	                    new Scalar("2", Type.I32, Operator.DELTA, ScalarValue.UNDEFINED, false),
	                    new Scalar("3", Type.I32, Operator.INCREMENT, new IntegerValue(10), false),
	                    new Scalar("4", Type.I32, Operator.INCREMENT, ScalarValue.UNDEFINED, false),
	                    new Scalar("5", Type.I32, Operator.CONSTANT, new IntegerValue(1), false), /* NON-TRANSFERRABLE */
	                    new Scalar("6", Type.I32, Operator.DEFAULT, new IntegerValue(2), false)
	                });
	        Context context = new Context();
	        context.registerTemplate(113, template);

	        FastEncoder encoder = new FastEncoder(context);

	        Message message = new Message(template);
	        message.setInteger(1, 109);
	        message.setInteger(2, 29470);
	        message.setInteger(3, 10);
	        message.setInteger(4, 3);
	        message.setInteger(5, 1);
	        message.setInteger(6, 2);

	        //             --PMAP-- --TID--- --------#1------- ------------#2------------ ---#4---
	        String msg1 = "11101000 11110001 00000000 11101101 00000001 01100110 10011110 10000011";
	        TestUtil.assertBitVectorEquals(msg1, encoder.encode(message));

	        message.setInteger(2, 29469);
	        message.setInteger(3, 11);
	        message.setInteger(4, 4);
	        message.setInteger(6, 3);

	        //             --PMAP-- ---#2--- ---#6---
	        String msg2 = "10000100 11111111 10000011";
	        TestUtil.assertBitVectorEquals(msg2, encoder.encode(message));

	        message.setInteger(1, 96);
	        message.setInteger(2, 30500);
	        message.setInteger(3, 12);
	        message.setInteger(4, 1);

	        //             --PMAP-- --------#1------- --------#2------- ---#4--- ---#6---
	        String msg3 = "10101100 00000000 11100000 00001000 10000111 10000001 10000011";
	        assertEquals(msg3, encoder.encode(message));
	    }
	   
	
}