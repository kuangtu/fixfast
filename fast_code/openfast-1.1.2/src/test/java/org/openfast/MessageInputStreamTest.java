package org.openfast;

import org.openfast.error.FastConstants;
import org.openfast.error.FastException;
import org.openfast.template.Field;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;

public class MessageInputStreamTest extends OpenFastTestCase {

    private final MessageTemplate template = new MessageTemplate("Simple", new Field[] { new Scalar("field", Type.ASCII, Operator.NONE, null, false)});

    public void testReadMessage() {
        MessageInputStream in = new MessageInputStream(bitStream("11000000 10000100"));
        try {
            in.readMessage();
            fail();
        } catch (FastException e) {
            assertEquals(FastConstants.D9_TEMPLATE_NOT_REGISTERED, e.getCode());
        }
    }
    
    public void testReadEOFReturnsNullWhenNoMoreMessagesInStream() {
        MessageInputStream in = new MessageInputStream(bitStream("11000000 10000001 10000000"));
        in.registerTemplate(1, template);
        Message msg = in.readMessage();
        assertNotNull(msg);
        Message msg2 = in.readMessage();
        assertNull(msg2);
    }
    
    public void testReadThrowsExceptionWhenPartialMessageInStream() {
        MessageInputStream in = new MessageInputStream(bitStream("11000000 10000001 10000000 10000000"));
        in.registerTemplate(1, template);
        assertEOF(in);
    }

    public void testReadThrowsExceptionWhenPartialMessageInStreamPartialAscii() {
        MessageInputStream in = new MessageInputStream(bitStream("11000000 10000001 10000000 10000000 00000001"));
        in.registerTemplate(1, template);
        assertEOF(in);
    }

    private void assertEOF(MessageInputStream in) {
        Message msg = in.readMessage();
        assertNotNull(msg);
        try {
            in.readMessage();
            fail("A FastException with END_OF_STREAM error code should have been thrown.");
        } catch (FastException e) {
            assertEquals(FastConstants.END_OF_STREAM, e.getCode());
        }
    }

}
