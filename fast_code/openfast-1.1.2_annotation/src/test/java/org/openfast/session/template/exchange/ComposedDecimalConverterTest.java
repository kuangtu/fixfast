package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.LongValue;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.util.Util;

import junit.framework.TestCase;

public class ComposedDecimalConverterTest extends TestCase {

	private ComposedDecimalConverter converter;
	private ConversionContext context;
	private ComposedScalar decimal;

	protected void setUp() throws Exception {
		converter = new ComposedDecimalConverter();
		context = SessionControlProtocol_1_1.createInitialContext();
		decimal = Util.composedDecimal(new QName("composed"), Operator.COPY, ScalarValue.UNDEFINED, Operator.DELTA, new LongValue(1000L), true);
	}
	
	public void testConvertGroupValueTemplateRegistryConversionContext() {
		GroupValue fieldDef = converter.convert(decimal, context);
		Field composedDecimal = converter.convert(fieldDef, TemplateRegistry.NULL, context);
		assertEquals(composedDecimal, decimal);
	}
}
