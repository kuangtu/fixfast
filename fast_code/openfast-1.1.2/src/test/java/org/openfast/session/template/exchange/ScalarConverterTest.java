package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import junit.framework.TestCase;

public class ScalarConverterTest extends TestCase {
    private ScalarConverter converter;
    private ConversionContext context;

    protected void setUp() throws Exception {
        converter = new ScalarConverter();
        context = SessionControlProtocol_1_1.createInitialContext();
    }

    public void testConvertDefaultWithDefaultValue() {
        Field scalar = new Scalar("default10", Type.U32, Operator.DEFAULT, new IntegerValue(10), false);
        GroupValue fieldDef = converter.convert(scalar, context);
        Field decodedScalar = converter.convert(fieldDef, TemplateRegistry.NULL, context);
        assertEquals(scalar, decodedScalar);
    }
    
    public void testConvertDeltaWithDefaultValue() {
        Field scalar = new Scalar("value", Type.U32, Operator.DELTA, new IntegerValue(1), false);
        GroupValue fieldDef = converter.convert(scalar, context);
        Field decodedScalar = converter.convert(fieldDef, TemplateRegistry.NULL, context);
        assertEquals(scalar, decodedScalar);
    }
}
