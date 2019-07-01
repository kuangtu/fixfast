package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.QName;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class GroupSerializerTest extends OpenFastTestCase {
    GroupSerializer serializer = new GroupSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerialize() {
        Group group = new Group(new QName("grp", "http://openfast.org"), new Field[] {
            new DynamicTemplateReference()
        }, true);
        group.setTypeReference(new QName("Set", "org.openfast"));
        serializer.serialize(writer, group, XMLMessageTemplateSerializer.createInitialContext());
        String expected =
            "<group name=\"grp\" ns=\"http://openfast.org\" presence=\"optional\">" + NL +
            "    <typeRef name=\"Set\" ns=\"org.openfast\"/>" + NL +
            "    <templateRef/>" + NL +
            "</group>" + NL;
        assertEquals(expected, byteOut.toString());
    }
}
