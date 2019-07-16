package org.openfast.template.loader;

import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class ComposedDecimalParserTest extends OpenFastTestCase {

    private FieldParser parser;
    private ParsingContext context;

    protected void setUp() throws Exception {
        parser = new ComposedDecimalParser();
        context = XMLMessageTemplateLoader.createInitialContext();
    }

    public void testParse() throws Exception {
        Element decimalDef = document(
                "<decimal name=\"composed\"><mantissa><delta/></mantissa><exponent><constant value=\"-2\"/></exponent></decimal>")
                .getDocumentElement();
        assertTrue(parser.canParse(decimalDef, context));
        ComposedScalar decimal = (ComposedScalar) parser.parse(decimalDef, context);
        assertComposedScalarField(decimal, Type.DECIMAL, "composed", Operator.CONSTANT, new IntegerValue(-2), Operator.DELTA,
                ScalarValue.UNDEFINED);
    }
    
    public void testParseEmptyMantissaAndExponent() throws Exception {

        Element decimalDef = document(
                "<decimal name=\"composed\"><mantissa> </mantissa><exponent> </exponent></decimal>")
                .getDocumentElement();
        assertTrue(parser.canParse(decimalDef, context));
        ComposedScalar decimal = (ComposedScalar) parser.parse(decimalDef, context);
        assertComposedScalarField(decimal, Type.DECIMAL, "composed", Operator.NONE, ScalarValue.UNDEFINED, Operator.NONE,
                ScalarValue.UNDEFINED);
    }

    public void testInheritDictionary() throws Exception {
        Element decimalDef = document(
                "<decimal name=\"composed\"><mantissa><delta/></mantissa><exponent><constant value=\"-2\"/></exponent></decimal>")
                .getDocumentElement();
        context.setDictionary("template");
        assertTrue(parser.canParse(decimalDef, context));
        ComposedScalar decimal = (ComposedScalar) parser.parse(decimalDef, context);
        assertComposedScalarField(decimal, Type.DECIMAL, "composed", Operator.CONSTANT, new IntegerValue(-2), Operator.DELTA,
                ScalarValue.UNDEFINED);
        assertEquals("template", decimal.getFields()[0].getDictionary());
        assertEquals("template", decimal.getFields()[1].getDictionary());
    }

    public void testFullyDefinedOperators() throws Exception {
        Element decimalDef = document(
                "<decimal name=\"composed\"><mantissa><delta dictionary=\"template\" key=\"variable\" value=\"100\"/></mantissa><exponent><copy dictionary=\"template\" key=\"static\" value=\"-2\"/></exponent></decimal>")
                .getDocumentElement();
        assertTrue(parser.canParse(decimalDef, context));
        ComposedScalar decimal = (ComposedScalar) parser.parse(decimalDef, context);

        assertComposedScalarField(decimal, Type.DECIMAL, "composed", Operator.COPY, new IntegerValue(-2), Operator.DELTA,
                new IntegerValue(100));

        Scalar exponent = decimal.getFields()[0];
        Scalar mantissa = decimal.getFields()[1];

        assertEquals("template", exponent.getDictionary());
        assertEquals(new QName("static"), exponent.getKey());

        assertEquals("template", exponent.getDictionary());
        assertEquals(new QName("variable"), mantissa.getKey());
    }
}
