package org.openfast.submitted;

import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class ConstantValueTest extends OpenFastTestCase {
    public void testConstantValues() {
        MessageTemplate template = template("<template name=\"ExecutionReport\" id=\"1\">" + "<typeRef name=\"asdf\"/>" +
                "<string name=\"constOpt\" id=\"13\" presence=\"optional\"> <constant value=\"13\"/> </string>" + 
                "<string name=\"const\" id=\"14\"> <constant value=\"12\"/> </string>" + 
                "</template>");
        FastDecoder decoder = decoder(template, bytes("11000000 10000001 10100000"));
        FastEncoder encoder = encoder(template);
        Message message = decoder.readMessage();
        assertEquals("12", message.getString("const"));
        assertEquals(null, message.getString("constOpt"));
        
        assertEquals("11000000 10000001", encoder.encode(message));
        
        message = decoder.readMessage();
        assertEquals("12", message.getString("const"));
        assertEquals("13", message.getString("constOpt"));
        
        assertEquals("10100000", encoder.encode(message));
    }
}
