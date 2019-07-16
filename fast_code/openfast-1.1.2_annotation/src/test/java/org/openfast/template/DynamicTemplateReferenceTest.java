package org.openfast.template;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.test.OpenFastTestCase;

public class DynamicTemplateReferenceTest extends OpenFastTestCase {

	private MessageTemplate nameTemplate;
	private MessageTemplate template;
	private Message message;
	private Message name;
	private Context context;

	protected void setUp() throws Exception {
		nameTemplate = template("<template>" +
						"  <string name=\"name\"/>" +
						"</template>");
		template = template("<template>" +
						"  <uInt32 name=\"quantity\"/>" +
						"  <templateRef />" +
						"  <decimal name=\"price\"/>" +
						"</template>");
		message = new Message(template);
		message.setInteger(1, 15);
		message.setDecimal(3, 102.0);

		name = new Message(nameTemplate);
		name.setString(1, "IBM");
		message.setFieldValue(2, name);

		context = new Context();
		context.registerTemplate(1, template);
		context.registerTemplate(2, nameTemplate);
	}
	
	public void testEncode() {
		FastEncoder encoder = new FastEncoder(context);
		//            --PMAP-- --TID--- ---#1--- --PMAP-- --TID--- ------------#1------------ ------------#3------------
		assertEquals("11000000 10000001 10001111 11000000 10000010 01001001 01000010 11001101 10000000 00000000 11100110", encoder.encode(message));
	}

	public void testDecode() {
		//                 --PMAP-- --TID--- ---#1--- --PMAP-- --TID--- ------------#1------------ ------------#3------------
		String encoding = "11000000 10000001 10001111 11000000 10000010 01001001 01000010 11001101 10000000 00000000 11100110"; 
		FastDecoder decoder = new FastDecoder(context, bitStream(encoding));
		Message readMessage = decoder.readMessage();
		assertEquals(message, readMessage);
	}

}
