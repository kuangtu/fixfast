package org.openfast.session.template.exchange;

import java.util.List;
import junit.framework.TestCase;
import org.openfast.GroupValue;
import org.openfast.Node;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.Util;

public class VariableLengthInstructionConverterTest extends TestCase {
    private VariableLengthInstructionConverter converter;
    private ConversionContext context;

    protected void setUp() throws Exception {
        converter = new VariableLengthInstructionConverter();
        context = SessionControlProtocol_1_1.createInitialContext();
    }

    public void testConvertOnByteVector() {
        Scalar bytes = new Scalar("bytes", Type.BYTE_VECTOR, Operator.NONE, ScalarValue.UNDEFINED, true);
        assertTrue(converter.shouldConvert(bytes));
        bytes.addNode(Util.createLength(new QName("numBytes"), null));
        GroupValue fieldDef = converter.convert(bytes, context);
        Scalar converted = (Scalar) converter.convert(fieldDef, TemplateRegistry.NULL, context);
        assertEquals(bytes, converted);
        List children = converted.getChildren(FastConstants.LENGTH_FIELD);
        assertEquals(1, children.size());
        Node lengthNode = (Node) children.get(0);
        assertEquals("numBytes", lengthNode.getAttribute(FastConstants.LENGTH_NAME_ATTR));
    }

    public void testConvertOnUnicodeString() {
        Scalar message = new Scalar("message", Type.UNICODE, Operator.COPY, ScalarValue.UNDEFINED, true);
        assertTrue(converter.shouldConvert(message));
        message.addNode(Util.createLength(new QName("messageLength"), null));
        GroupValue fieldDef = converter.convert(message, context);
        Scalar converted = (Scalar) converter.convert(fieldDef, TemplateRegistry.NULL, context);
        assertEquals(message, converted);
        List children = converted.getChildren(FastConstants.LENGTH_FIELD);
        assertEquals(1, children.size());
        Node lengthNode = (Node) children.get(0);
        assertEquals("messageLength", lengthNode.getAttribute(FastConstants.LENGTH_NAME_ATTR));
    }
}
