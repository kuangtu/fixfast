package org.openfast.submitted;

import org.openfast.Message;
import org.openfast.StringValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TailOperatorTest extends OpenFastTestCase {
	
	public void testSameValue() {
		MessageTemplate template = new MessageTemplate("", new Field[] {
			new Scalar("mandInit1", Type.ASCII, Operator.TAIL, new StringValue("abcXXX"), false),
			new Scalar("mandInit2", Type.ASCII, Operator.TAIL, new StringValue("defXXX"), false)
		});
		FastEncoder encoder = encoder(template);
		
		Message message = new Message(template);
		message.setString("mandInit1", "abcXXX");
		message.setString("mandInit2", "defYYY");
		
		byte[] encoding = encoder.encode(message);
		assertEquals("11010000 10000001 01011001 01011001 11011001", encoding);
		
		encoding = encoder.encode(message);
		assertEquals("10000000", encoding);
	}
	
	public void testOptionalFields() {
		MessageTemplate template = new MessageTemplate("", new Field[] {
				new Scalar("mandInit1", Type.ASCII, Operator.TAIL, new StringValue("abcXXX"), false),
				new Scalar("mandInit2", Type.ASCII, Operator.TAIL, new StringValue("defXXX"), false)
			});
		FastEncoder encoder = encoder(template);
		
		Message message = new Message(template);
		message.setString("mandInit1", "abcXXX");
		message.setString("mandInit2", "defYYY");
		
		byte[] encoding = encoder.encode(message);
		assertEquals("11010000 10000001 01011001 01011001 11011001", encoding);
		
		encoding = encoder.encode(message);
		assertEquals("10000000", encoding);
	}
}
