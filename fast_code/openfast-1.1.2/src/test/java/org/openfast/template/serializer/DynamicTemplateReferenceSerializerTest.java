package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.template.DynamicTemplateReference;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class DynamicTemplateReferenceSerializerTest extends OpenFastTestCase {
    DynamicTemplateReferenceSerializer serializer = new DynamicTemplateReferenceSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerialize() {
        serializer.serialize(writer, new DynamicTemplateReference(), XMLMessageTemplateSerializer.createInitialContext());
        assertEquals("<templateRef/>" + NL, byteOut.toString());
    }
}
