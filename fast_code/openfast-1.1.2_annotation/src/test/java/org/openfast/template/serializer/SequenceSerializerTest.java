package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.Node;
import org.openfast.QName;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Sequence;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.Util;
import org.openfast.util.XmlWriter;

public class SequenceSerializerTest extends OpenFastTestCase {
    SequenceSerializer serializer = new SequenceSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeFull() {
        Sequence sequence = new Sequence(new QName("Parties", "http://openfast.org"), new Field[] {
            new DynamicTemplateReference()
        }, true);
        sequence.setTypeReference(new QName("Party", "org.openfast"));
        Node lengthNode = Util.createLength(new QName("NoParties", "http://www.openfast.org"), "100");
        sequence.addNode(lengthNode);
        String expected =
            "<sequence name=\"Parties\" ns=\"http://openfast.org\" presence=\"optional\">" + NL +
            "    <typeRef name=\"Party\" ns=\"org.openfast\"/>" + NL +
            "    <length name=\"NoParties\" ns=\"http://www.openfast.org\" id=\"100\"/>" + NL +
            "    <templateRef/>" + NL +
            "</sequence>" + NL;
        serializer.serialize(writer, sequence, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
}
