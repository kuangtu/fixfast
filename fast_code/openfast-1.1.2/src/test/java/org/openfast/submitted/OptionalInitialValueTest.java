package org.openfast.submitted;

import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class OptionalInitialValueTest extends OpenFastTestCase {

	public void testOptionalInitialValue() throws Exception {
		MessageTemplate template = template("<template name=\"OptCopyInit\" id=\"520\" dictionary=\"template\">" +
	        "<string id=\"1\" presence=\"optional\" name=\"Line1\"><copy value=\"abc\"/></string>" +
	        "<string id=\"1\" presence=\"optional\" name=\"Line2\"><copy value=\"abc\"/></string>" +
	        "<string id=\"1\" presence=\"optional\" name=\"Line3\"><copy value=\"abc\"/></string>" +
        "</template>");
		FastEncoder encoder = encoder(template);
		Message m = new Message(template);
        m.setString("Line1", null);  // absent     
        m.setString("Line2", "abc"); // as initial  
        m.setString("Line3", "xyz"); // sth else  
		byte[] encoding = encoder.encode(m);
		
		Message decoded = decoder(template, encoding).readMessage();
		assertEquals(m, decoded);
		
		encoding = encoder.encode(m);
		assertEquals("10000000", encoding);
	}
	
	public void testOptionalNoInitialValue() throws Exception {
		MessageTemplate template = template("<template name=\"OptCopyInit\" id=\"520\" dictionary=\"template\">" +
				"<string id=\"1\" presence=\"optional\" name=\"Line1\"><copy/></string>" +
				"<string id=\"1\" presence=\"optional\" name=\"Line2\"><copy value=\"abc\"/></string>" +   
				"<string id=\"1\" presence=\"optional\" name=\"Line3\"><copy value=\"abc\"/></string>" +   
			"</template>");
		FastEncoder encoder = encoder(template);
		Message m = new Message(template);
		m.setString("Line1", null);  // absent
		m.setString("Line2", "abc"); // as initial  
		m.setString("Line3", "xyz"); // sth else  
		byte[] encoding = encoder.encode(m);
		
		assertEquals("11001000 10000001 01111000 01111001 11111010", encoding);
		
		Message decoded = decoder(template, encoding).readMessage();
		assertEquals(m, decoded);
	}
	
	public void testOptionalInitialValueForTail() throws Exception {
		MessageTemplate template = template("<template name=\"OptCopyInit\" id=\"520\" dictionary=\"template\">" +
	        "<string id=\"1\" presence=\"optional\" name=\"Line1\"><tail/></string>" +
	        "<string id=\"1\" presence=\"optional\" name=\"Line2\"><tail value=\"abc\"/></string>" +
        "</template>");
		FastEncoder encoder = encoder(template);
		Message m = new Message(template); // all fields null
		byte[] encoding = encoder.encode(m);
		assertEquals("11010000 10000001 10000000", encoding);
		
		Message decoded = decoder(template, encoding).readMessage();
		assertEquals(m, decoded);
	}
}
