package org.openfast.submitted;

import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class StringNullTest extends OpenFastTestCase {
	
	public void testMandatoryStringWithEmptyString() throws Exception {
		MessageTemplate template = new MessageTemplate("template", new Field[] {
			new Scalar("string", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, false)
		});
		FastEncoder encoder = encoder(template);
		Message message = new Message(template);
		message.setString("string", "\u0000");
		byte[] encoding = encoder.encode(message);
		assertEquals("11100000 10000001 00000000 10000000", encoding);
		
		FastDecoder decoder = decoder(template, encoding);
		assertEquals("\u0000", decoder.readMessage().getString(1));
		
		message.setString("string", "");
		encoder.reset();
		encoding = encoder.encode(message);
		assertEquals("11100000 10000001 10000000", encoding);
		decoder = decoder(template, encoding);
		assertEquals("", decoder.readMessage().getString(1));
	}
	
	public void testOptionalStringWithEmptyString() throws Exception {
		MessageTemplate template = new MessageTemplate("template", new Field[] {
				new Scalar("string", Type.ASCII, Operator.COPY, ScalarValue.UNDEFINED, true)
			});
		FastEncoder encoder = encoder(template);
		Message message = new Message(template);
		message.setString("string", "\u0000");
		byte[] encoding = encoder.encode(message);
		assertEquals("11100000 10000001 00000000 00000000 10000000", encoding);
		
		FastDecoder decoder = decoder(template, encoding);
		assertEquals("\u0000", decoder.readMessage().getString(1));
		
		message.setString("string", "");
		encoder.reset();
		encoding = encoder.encode(message);
		assertEquals("11100000 10000001 00000000 10000000", encoding);
		decoder = decoder(template, encoding);
		assertEquals("", decoder.readMessage().getString(1));
	}
}
