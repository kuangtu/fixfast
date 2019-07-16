package org.openfast.template.type;

import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class StringTypeTest extends OpenFastTestCase {
    public void testStringWithLength() {
        MessageTemplate template = template("<template name=\"template\">"
                + "  <string name=\"message\" charset=\"unicode\"><length name=\"messageLength\"/><copy/></string>" + "</template>");
        FastDecoder decoder = decoder("11100000 10000001 10000010 01010101 10101010", template);
        Message message = decoder.readMessage();
        assertEquals(2, message.getInt("messageLength"));
    }
}
