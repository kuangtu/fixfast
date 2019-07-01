package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.Dictionary;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.SimpleNode;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.XmlWriter;

public class VariableLengthScalarSerializerTest extends OpenFastTestCase {
    AbstractFieldSerializer serializer = new VariableLengthScalarSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeString() {
        Scalar scalar = new Scalar(new QName("name", "http://openfast.org"), Type.UNICODE, Operator.COPY, new StringValue("abcd"), false);
        scalar.setKey(new QName("items", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("5");
        String expected = 
            "<string name=\"name\" ns=\"http://openfast.org\" id=\"5\" charset=\"unicode\">" + NL +
            "    <copy dictionary=\"template\" key=\"items\" ns=\"http://www.openfast.org\" value=\"abcd\"/>" + NL +
            "</string>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeByteVector() {
        Scalar scalar = new Scalar(new QName("bytes", "http://openfast.org"), Type.BYTE_VECTOR, Operator.TAIL, ScalarValue.UNDEFINED, true);
        scalar.setKey(new QName("items", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("345");
        SimpleNode node = new SimpleNode(FastConstants.LENGTH_FIELD);
        node.setAttribute(new QName("name", FastConstants.TEMPLATE_DEFINITION_1_1), "bytesLength");
        node.setAttribute(new QName("namespace", FastConstants.TEMPLATE_DEFINITION_1_1), "http://www.openfast.org");
        node.setAttribute(new QName("id", FastConstants.TEMPLATE_DEFINITION_1_1), "344");
        scalar.addNode(node);
        String expected = 
            "<byteVector name=\"bytes\" ns=\"http://openfast.org\" id=\"345\" presence=\"optional\">" + NL +
            "    <length name=\"bytesLength\" ns=\"http://www.openfast.org\" id=\"344\"/>" + NL +
            "    <tail dictionary=\"template\" key=\"items\" ns=\"http://www.openfast.org\"/>" + NL +
            "</byteVector>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
}
