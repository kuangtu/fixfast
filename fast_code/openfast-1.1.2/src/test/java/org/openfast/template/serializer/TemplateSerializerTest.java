package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.QName;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class TemplateSerializerTest extends OpenFastTestCase {
    TemplateSerializer serializer = new TemplateSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeNs() {
        MessageTemplate template = new MessageTemplate(new QName("reset", "http://www.openfast.org/templates"), 
                new Field[] { new DynamicTemplateReference() });
        template.setTypeReference(new QName("Reset", "org.openfast"));
        template.setId("16001");
        serializer.serialize(writer, template, XMLMessageTemplateSerializer.createInitialContext());
        String expected = 
            "<template name=\"reset\" templateNs=\"http://www.openfast.org/templates\" id=\"16001\">" + NL +
            "    <typeRef name=\"Reset\" ns=\"org.openfast\"/>" + NL +
            "    <templateRef/>" + NL +
            "</template>" + NL;
        assertEquals(expected, byteOut.toString());
    }
}
