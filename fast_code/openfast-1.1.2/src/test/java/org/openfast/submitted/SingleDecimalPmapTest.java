package org.openfast.submitted;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class SingleDecimalPmapTest extends OpenFastTestCase {
    public void testSingleOptionalDecimalAlwaysPresentExponent() {
        MessageTemplate template = template(
                "<template name=\"tmpl\">" + 
                "    <group name=\"grp\">" +
                "        <decimal name=\"dcml\">" +
                "            <exponent><delta/></exponent>" +
                "            <mantissa><copy/></mantissa>" +
                "        </decimal>" +
                "    </group>" + 
                "</template>");
        assertTrue(template.getGroup("grp").usesPresenceMap());
        FastEncoder encoder = encoder(template);
        Message message = new Message(template);
        GroupValue grp = new GroupValue(template.getGroup("grp"));
        message.setFieldValue("grp", grp);
        grp.setDecimal(0, 2.0);
        assertEquals("11000000 10000001 11000000 10000000 10000010", encoder.encode(message));
    }
}
