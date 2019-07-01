package org.openfast.template.serializer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.operator.Operator;
import org.openfast.test.OpenFastTestCase;
import org.openfast.util.Util;
import org.openfast.util.XmlWriter;

public class ComposedDecimalSerializerTest extends OpenFastTestCase {
    ComposedDecimalSerializer serializer = new ComposedDecimalSerializer();
    OutputStream byteOut = new ByteArrayOutputStream();
    XmlWriter writer = new XmlWriter(byteOut);
    
    public void testSerializeFull() {
        ComposedScalar decimal =Util.composedDecimal(new QName("price", "http://openfast.org"), Operator.CONSTANT, new IntegerValue(2), Operator.DELTA, ScalarValue.UNDEFINED, true);
        serializer.serialize(writer, decimal, XMLMessageTemplateSerializer.createInitialContext());
        String expected = 
            "<decimal name=\"price\" ns=\"http://openfast.org\" presence=\"optional\">" + NL +
            "    <exponent>" + NL +
            "        <constant value=\"2\"/>" + NL +
            "    </exponent>" + NL +
            "    <mantissa>" + NL +
            "        <delta/>" + NL +
            "    </mantissa>" + NL +
            "</decimal>" + NL;
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeExponentOperator() {
        ComposedScalar decimal =Util.composedDecimal(new QName("price"), Operator.CONSTANT, new IntegerValue(5), Operator.NONE, ScalarValue.UNDEFINED, false);
        serializer.serialize(writer, decimal, XMLMessageTemplateSerializer.createInitialContext());
        String expected = 
            "<decimal name=\"price\">" + NL +
            "    <exponent>" + NL +
            "        <constant value=\"5\"/>" + NL +
            "    </exponent>" + NL +
            "</decimal>" + NL;
        assertEquals(expected, byteOut.toString());
    }
    
    public void testSerializeMantissaOperator() {
        ComposedScalar decimal =Util.composedDecimal(new QName("price"), Operator.NONE, ScalarValue.UNDEFINED, Operator.COPY, ScalarValue.UNDEFINED, false);
        decimal.getFields()[1].setKey(new QName("values"));
        serializer.serialize(writer, decimal, XMLMessageTemplateSerializer.createInitialContext());
        String expected = 
            "<decimal name=\"price\">" + NL +
            "    <mantissa>" + NL +
            "        <copy key=\"values\"/>" + NL +
            "    </mantissa>" + NL +
            "</decimal>" + NL;
        assertEquals(expected, byteOut.toString());
    }
}
