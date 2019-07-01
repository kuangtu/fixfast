package org.openfast.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.test.OpenFastTestCase;

public class XmlWriterTest extends OpenFastTestCase {
    public void testSimpleXml() {
        OutputStream byteOut = new ByteArrayOutputStream();
        XmlWriter writer = new XmlWriter(byteOut);
        writer.start("templates");
        writer.start("template");
        writer.addAttribute("name", "reset");
        writer.end();
        writer.end();
        String xml = 
            "<templates>" + NL +
            "    <template name=\"reset\"/>" + NL +
            "</templates>" + NL;
        assertEquals(xml, byteOut.toString());
    }
}
