package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.Dictionary;
import org.openfast.IntegerValue;
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

public class ScalarSerializerTest extends OpenFastTestCase {
    ScalarSerializer serializer = new ScalarSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeFull() {
        Scalar scalar = new Scalar(new QName("value", "http://openfast.org"), Type.U32, Operator.DELTA, new IntegerValue(100), true);
        scalar.setKey(new QName("data", "http://www.openfast.org"));
        scalar.setDictionary(Dictionary.TEMPLATE);
        scalar.setId("101");
        String expected = 
            "<uInt32 name=\"value\" ns=\"http://openfast.org\" id=\"101\" presence=\"optional\">" + NL +
            "    <delta dictionary=\"template\" key=\"data\" ns=\"http://www.openfast.org\" value=\"100\"/>" + NL +
            "</uInt32>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeBasic() {
        Scalar scalar = new Scalar("simple", Type.DECIMAL, Operator.NONE, ScalarValue.UNDEFINED, false);
        String expected = "<decimal name=\"simple\"/>" + NL;
        serializer.serialize(writer, scalar, XMLMessageTemplateSerializer.createInitialContext());
        assertEquals(expected, byteOut.toString());
    }
}
