package org.openfast.submitted;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.openfast.Message;
import org.openfast.MessageInputStream;
import org.openfast.MessageOutputStream;
import org.openfast.ScalarValue;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class TailOperatorWithRepeatedValuesTest extends OpenFastTestCase {

	public void testTailOperatorWithRepeatedValues() {
		MessageTemplate t = new MessageTemplate("temp", new Field[] {
			new Scalar("main", Type.ASCII, Operator.TAIL, ScalarValue.UNDEFINED, false)
		});
		Message m = new Message(t);
		m.setString(1, "abcde");
		
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		MessageOutputStream messageOut = new MessageOutputStream(byteOut);
		messageOut.registerTemplate(1, t);
		
		messageOut.writeMessage(m);
		messageOut.writeMessage(m);
		
		MessageInputStream messageIn = new MessageInputStream(new ByteArrayInputStream(byteOut.toByteArray()));
		messageIn.registerTemplate(1, t);
		
		assertEquals(m, messageIn.readMessage());
		assertEquals(m, messageIn.readMessage());
	}
}
