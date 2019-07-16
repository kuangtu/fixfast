package org.openfast.submitted;

import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.QName;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.operator.Operator;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.Util;

public class LargeValuesTest extends OpenFastTestCase {

    public void testLargeDecimal() {
        MessageTemplate composed = 
            template(Util.composedDecimal(new QName("Line1"), Operator.COPY, new IntegerValue(-2), 
                                                              Operator.COPY, new IntegerValue(94276), true));
        FastEncoder encoder = encoder(composed);
        Message message = (Message) composed.createValue(null);
        message.setDecimal(1, 987654321.123456);
        
        byte[] encoded = encoder.encode(message);
        FastDecoder decoder = decoder(composed, encoded);
        assertEquals(message, decoder.readMessage());
    }
}
