package org.openfast.template.loader;

import org.openfast.Dictionary;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class StringParserTest extends OpenFastTestCase {
    private ScalarParser parser;
    private ParsingContext context;

    protected void setUp() throws Exception {
        parser = new StringParser();
        context = XMLMessageTemplateLoader.createInitialContext();
    }

    public void testParse() throws Exception {
        Element unicodeDef = document("<string name=\"message\" charset=\"unicode\"/>").getDocumentElement();
        assertTrue(parser.canParse(unicodeDef, context));
        Scalar unicode = (Scalar) parser.parse(unicodeDef, context);
        assertScalarField(unicode, Type.UNICODE, "message", null, "", Dictionary.GLOBAL, "message", Operator.NONE,
                ScalarValue.UNDEFINED, false);
    }
}
