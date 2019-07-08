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

public class FASTDecodeProcTest extends OpenFastTestCase {

	public void testDecodeProcess() {
		  MessageTemplate template1 = new MessageTemplate("", new Field[] {
	                new Scalar("1", Type.U32, Operator.COPY, ScalarValue.UNDEFINED, false),
	                new Scalar("2", Type.U32, Operator.DELTA, ScalarValue.UNDEFINED, false),
	                new Scalar("3", Type.U32, Operator.INCREMENT, new IntegerValue(10), false),
	                new Scalar("4", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, false),
	                new Scalar("5", Type.U32, Operator.DEFAULT, new IntegerValue(2), true) });

	        MessageTemplate template2 = new MessageTemplate("", new Field[] {
	                new Scalar("1", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false),
	                new Scalar("2", Type.ASCII, Operator.DELTA, ScalarValue.UNDEFINED, false),
	                new Scalar("3", Type.ASCII, Operator.DEFAULT, new StringValue("long"), true) });
	        
	                     // --PMAP-- --TID--- ---#1--- ------------#2------------ ---#4---
	        String msg1 = "11101000 10001010 10001011 00000001 01100110 10011110 10000011";

	                     // --PMAP-- ---#2--- ---#5---
	        String msg2 = "10000100 10000011 10000100";

	                       // --PMAP-- ---#1--- --------#2------- ---#4--- ---#6---
	        String msg3 = "10101100 11100000 00001000 10000111 10000001 10000011";

	        InputStream in = ByteUtil.createByteStream(msg1 + ' ' + msg2 + ' ' + msg3);
	        Context context = new Context();
	        context.registerTemplate(10, template1);
	        context.registerTemplate(11, template2);

	        FastDecoder decoder = new FastDecoder(context, in);

	        Message msg_t1 = decoder.readMessage();
	        assertEquals(10, msg_t1.getInt(0));
	        assertEquals(11, msg_t1.getInt(1));
	        assertEquals(29470,  msg_t1.getInt(2));
	        assertEquals(10, msg_t1.getInt(3));
	        assertEquals(3, msg_t1.getInt(4));
	        assertEquals(2, msg_t1.getInt(5));
	        
	        msg_t1 = decoder.readMessage();
	        assertEquals(10, msg_t1.getInt(0));
	        assertEquals(11, msg_t1.getInt(1));
	        assertEquals(29473,  msg_t1.getInt(2));
	        assertEquals(11, msg_t1.getInt(3));
	        assertEquals(4, msg_t1.getInt(4));
	        assertEquals(3, msg_t1.getInt(5));
	        
	        msg_t1 = decoder.readMessage();
	        assertEquals(10, msg_t1.getInt(0));
	        assertEquals(96, msg_t1.getInt(1));
	        assertEquals(30504,  msg_t1.getInt(2));
	        assertEquals(12, msg_t1.getInt(3));
	        assertEquals(1, msg_t1.getInt(4));
	        assertEquals(2, msg_t1.getInt(5));

	}
	
}