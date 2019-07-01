package org.openfast.template.loader;

import org.openfast.DecimalValue;
import org.openfast.Dictionary;
import org.openfast.ScalarValue;
import org.openfast.error.FastException;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.test.OpenFastTestCase;
import org.w3c.dom.Element;

public class ScalarParserTest extends OpenFastTestCase {

	private ScalarParser parser;
	private ParsingContext context;

	protected void setUp() throws Exception {
		parser = new ScalarParser();
		context = XMLMessageTemplateLoader.createInitialContext();
	}
	
	public void testParseCopyInt() throws Exception {
		Element int32Def = document("<int32 name=\"value\"><copy/></int32>").getDocumentElement();
		assertTrue(parser.canParse(int32Def, context));
		Scalar int32 = (Scalar) parser.parse(int32Def, context);
		assertScalarField(int32, Type.I32, "value", null, "", Dictionary.GLOBAL, "value", Operator.COPY, ScalarValue.UNDEFINED, false);
	}
	
	public void testParseDeltaDecimal() throws Exception {
		Element decimalDef = document("<decimal name=\"price\"><delta value=\"1.2\" key=\"decimalValue\" dictionary=\"marketData\"/></decimal>").getDocumentElement();
		assertTrue(parser.canParse(decimalDef, context));
		Scalar decimal = (Scalar) parser.parse(decimalDef, context);
		assertScalarField(decimal, Type.DECIMAL, "price", null, "", "marketData", "decimalValue", Operator.DELTA, new DecimalValue(1.2), false);
	}

	public void testParseStringDefaultWithNamespace() throws Exception {
		Element stringDef = document("<string name=\"text\" ns=\"http://openfast.org/data/\" presence=\"optional\"><default/></string>").getDocumentElement();
		assertTrue(parser.canParse(stringDef, context));
		Scalar string = (Scalar) parser.parse(stringDef, context);
		assertScalarField(string, Type.STRING, "text", null, "http://openfast.org/data/", Dictionary.GLOBAL, "text", Operator.DEFAULT, ScalarValue.UNDEFINED, true);
	}
	
	public void testParseWithOperatorNamespace() throws Exception {
		Element uintDef = document("<uInt32 name=\"uint\"><copy key=\"values\" ns=\"http://openfast.org/data/\"/></uInt32>").getDocumentElement();
		Scalar uint = (Scalar) parser.parse(uintDef, context);
		assertScalarField(uint, Type.U32, "uint", null, "", Dictionary.GLOBAL, "values", "http://openfast.org/data/", Operator.COPY, ScalarValue.UNDEFINED, false);
	}
	
	public void testInvalidType() throws Exception {
		Element invalidDef = document("<array name=\"set\"/>").getDocumentElement();
		try {
			parser.parse(invalidDef, context);
		} catch (FastException e) {
			assertEquals(XMLMessageTemplateLoader.INVALID_TYPE, e.getCode());
			assertTrue(e.getMessage().startsWith("The type array is not defined.  Possible types: "));
		}
	}
}
