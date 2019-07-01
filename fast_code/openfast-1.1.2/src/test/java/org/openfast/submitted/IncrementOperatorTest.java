package org.openfast.submitted;

import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.ScalarValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class IncrementOperatorTest extends OpenFastTestCase {
	public void testOptionalIncrementWithNoInitialValue() {
		MessageTemplate template = new MessageTemplate("t", new Field[] {
			new Scalar("1", Type.U32, Operator.INCREMENT, ScalarValue.UNDEFINED, true),
		});
		FastEncoder encoder = encoder(template);
		Message message = new Message(template);
		assertEquals("11000000 10000001", encoder.encode(message));
	}
	
	public void testOptionalIncrementWithInitialValue() {
		MessageTemplate template = new MessageTemplate("t", new Field[] {
			new Scalar("1", Type.U32, Operator.INCREMENT, new IntegerValue(1), true),
		});
		FastEncoder encoder = encoder(template);
		Message message = new Message(template);
		assertEquals("11100000 10000001 10000000", encoder.encode(message));
		
		encoder.reset();
		message.setFieldValue(1, new IntegerValue(1));
		assertEquals("11000000 10000001", encoder.encode(message));
	}
}
