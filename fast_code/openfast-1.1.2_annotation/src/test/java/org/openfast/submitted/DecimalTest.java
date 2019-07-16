package org.openfast.submitted;

import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.test.OpenFastTestCase;

public class DecimalTest extends OpenFastTestCase {
    
    public void testDecimalDecode() {
        MessageTemplate template = template("<template name=\"DecimalTest\" id = \"13\">" +
//            "<decimal name=\"IncrementDec\" id = \"1013\" >" +
//            "    <exponent> <increment value =\"1\"/> </exponent>" +
//            "    <mantissa> <increment value =\"-10\"/> </mantissa>" +
//            "</decimal>" +
//            "<decimal name=\"DeltaDec\" id = \"1014\" >" +
//            "    <exponent> <delta value =\"-2\"/> </exponent>" +
//            "    <mantissa> <delta value =\"1337\"/> </mantissa>" +
//            "</decimal>" +
//            "<decimal name=\"CopyDec\" id = \"1015\" >" +
//            "    <exponent> <copy value =\"3\"/> </exponent>" +
//            "    <mantissa> <copy value =\"10\"/> </mantissa>" +
//            "</decimal>" +
            "<decimal name=\"DefaultDec\" id = \"1016\" >" +
            "    <exponent> <default value =\"2\"/> </exponent>" +
            "    <mantissa> <default value =\"1\"/> </mantissa>" +
            "</decimal>" +
            "</template>");
        Message message = new Message(template);
//        message.setDecimal("IncrementDec", -10.0);
//        message.setDecimal("DeltaDec", 13.37);
//        message.setDecimal("CopyDec", 10000.0);
        message.setDecimal("DefaultDec", 100.0);
        FastEncoder encoder = encoder(template);
        //                   PMAP    Temp ID    -1        0        0        4        1       1
//        String expected = "11011101 10000001 11111111 10000000 10000000 10000100 10000001 10000001";
        String expected = "11000000 10000001";
        assertEquals(expected, encoder.encode(message));
    }
    
    public void testInitialValue() {
        MessageTemplate template = template("<template>" + 
        "<decimal id=\"1\" presence=\"optional\" name=\"Line1\">" + 
             "<exponent><copy value=\"-2\"/></exponent> " +
             "<mantissa><copy value=\"94276\"/></mantissa> " +
        "</decimal></template>");
        FastEncoder encoder = encoder(template);
        Message m = (Message) template.createValue(null);
        m.setDecimal("Line1", 9427.6 ); 
        assertEquals("11100000 10000001 11111111", encoder.encode(m));
    }
}
