package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class XMLMessageTemplateSerializerTest extends OpenFastTestCase {
    XMLMessageTemplateSerializer serializer = new XMLMessageTemplateSerializer();
    public void testSerialize() {
        MessageTemplate template = new MessageTemplate("reset", new Field[] {});
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        serializer.serialize(new MessageTemplate[] { template }, byteOut);
        String expected = 
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + NL +
            "<templates xmlns=\"http://www.fixprotocol.org/ns/fast/td/1.1\">" + NL +
            "    <template name=\"reset\"/>" + NL +
            "</templates>" + NL;
        assertEquals(expected, byteOut.toString());
    }
}
