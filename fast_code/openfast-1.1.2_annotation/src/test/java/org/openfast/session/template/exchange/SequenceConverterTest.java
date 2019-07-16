package org.openfast.session.template.exchange;

import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import junit.framework.TestCase;

public class SequenceConverterTest extends TestCase {
    private SequenceConverter converter;
    private ConversionContext context;

    protected void setUp() throws Exception {
        converter = new SequenceConverter();
        context = SessionControlProtocol_1_1.createInitialContext();
    }

    public void testConvert() {
        Scalar lengthField = new Scalar("listSize", Type.U32, Operator.COPY, new IntegerValue(10), false);
        Sequence sequence = new Sequence(new QName("list"), lengthField, new Field[] { new Scalar("item", Type.DECIMAL,
                Operator.DELTA, ScalarValue.UNDEFINED, false) }, false);
        assertTrue(converter.shouldConvert(sequence));
        GroupValue fieldDef = converter.convert(sequence, context);
        Sequence converted = (Sequence) converter.convert(fieldDef, TemplateRegistry.NULL, context);
        assertEquals(sequence, converted);
    }
}
