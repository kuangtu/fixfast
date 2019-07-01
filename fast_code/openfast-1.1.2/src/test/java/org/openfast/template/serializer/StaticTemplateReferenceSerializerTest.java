package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.QName;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.StaticTemplateReference;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class StaticTemplateReferenceSerializerTest extends OpenFastTestCase {
    StaticTemplateReferenceSerializer serializer = new StaticTemplateReferenceSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerialize() {
        MessageTemplate template = new MessageTemplate(new QName("NsName", "http://www.openfast.org"), new Field[] {});
        StaticTemplateReference ref = new StaticTemplateReference(template);
        serializer.serialize(writer, ref, XMLMessageTemplateSerializer.createInitialContext());
        String expected = "<templateRef name=\"NsName\" ns=\"http://www.openfast.org\"/>" + NL;
        assertEquals(expected, byteOut.toString());
    }
}
