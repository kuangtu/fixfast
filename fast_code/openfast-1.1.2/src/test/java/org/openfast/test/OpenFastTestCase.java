/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */

package org.openfast.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.Context;
import org.openfast.DecimalValue;
import org.openfast.IntegerValue;
import org.openfast.QName;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.codec.FastDecoder;
import org.openfast.codec.FastEncoder;
import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.FieldSet;
import org.openfast.template.Group;
import org.openfast.template.LongValue;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.template.TwinValue;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.openfast.template.operator.Operator;
import org.openfast.template.operator.OperatorCodec;
import org.openfast.template.type.Type;
import org.openfast.template.type.codec.TypeCodec;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class OpenFastTestCase extends TestCase {
    protected static final String NL = System.getProperty("line.separator");
    protected static final ScalarValue NULL = ScalarValue.NULL;

    protected static final ScalarValue UNDEF = ScalarValue.UNDEFINED;

    public static DecimalValue d(double value) {
        return new DecimalValue(value);
    }

    protected static IntegerValue i(int value) {
        return new IntegerValue(value);
    }

    protected static LongValue l(long value) {
        return new LongValue(value);
    }

    protected static TwinValue twin(ScalarValue first, ScalarValue second) {
        return new TwinValue(first, second);
    }

    protected static void assertEquals(String bitString, byte[] encoding) {
        TestUtil.assertBitVectorEquals(bitString, encoding);
    }

    protected static void assertEncodeDecode(ScalarValue value, String bitString, TypeCodec type) {
        assertEquals(bitString, type.encode(value == null ? ScalarValue.NULL : value));
        assertEquals(value, type.decode(ByteUtil.createByteStream(bitString)));
    }

    protected static InputStream bitStream(String bitString) {
        return ByteUtil.createByteStream(bitString);
    }

    protected static InputStream stream(String source) {
        return new ByteArrayInputStream(source.getBytes());
    }

    protected static InputStream byteStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    protected static ByteVectorValue byt(byte[] value) {
        return new ByteVectorValue(value);
    }

    protected static FastDecoder decoder(String bitString, MessageTemplate template) {
        Context context = new Context();
        context.registerTemplate(1, template);
        return new FastDecoder(context, bitStream(bitString));
    }

    protected static FastDecoder decoder(MessageTemplate template, byte[] encoding) {
        Context context = new Context();
        context.registerTemplate(1, template);
        return new FastDecoder(context, new ByteArrayInputStream(encoding));
    }

    protected static FastEncoder encoder(MessageTemplate template) {
        Context context = new Context();
        context.registerTemplate(1, template);
        return new FastEncoder(context);
    }

    protected static Date date(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    protected static Date time(int hour, int min, int sec, int ms) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, ms);
        return cal.getTime();
    }

    protected byte[] byt(String hexString) {
        return ByteUtil.convertHexStringToByteArray(hexString);
    }

    protected byte[] bytes(String binaryString) {
        return ByteUtil.convertBitStringToFastByteArray(binaryString);
    }

    protected DecimalValue d(int mantissa, int exponent) {
        return new DecimalValue(mantissa, exponent);
    }

    protected ScalarValue string(String value) {
        return new StringValue(value);
    }

    protected MessageTemplate template(String templateXml) {
        MessageTemplate[] templates = new XMLMessageTemplateLoader().load(new ByteArrayInputStream(templateXml.getBytes()));
        return templates[0];
    }

    protected MessageTemplate template(Field field) {
        return new MessageTemplate("Doesn't matter", new Field[] { field });
    }

    protected void assertScalarField(FieldSet fieldSet, int fieldIndex, Type type, String name, OperatorCodec operator,
            ScalarValue defaultValue) {
        Scalar field = (Scalar) fieldSet.getField(fieldIndex);
        assertScalarField(field, type, name);
        assertEquals(operator, field.getOperatorCodec());
        assertEquals(defaultValue, field.getDefaultValue());
    }

    protected void assertComposedScalarField(FieldSet fieldSet, int fieldIndex, Type type, String name, Operator exponentOp,
            ScalarValue exponentValue, Operator mantissaOp, ScalarValue mantissaValue) {
        ComposedScalar field = (ComposedScalar) fieldSet.getField(fieldIndex);
        assertEquals(type, field.getType());
        assertEquals(name, field.getName());
        Scalar[] fields = field.getFields();
        assertEquals(exponentOp, fields[0].getOperator());
        assertEquals(exponentValue, fields[0].getDefaultValue());

        assertEquals(mantissaOp, fields[1].getOperator());
        assertEquals(mantissaValue, fields[1].getDefaultValue());
    }

    protected void assertComposedScalarField(ComposedScalar field, Type type, String name, Operator exponentOp,
            ScalarValue exponentValue, Operator mantissaOp, ScalarValue mantissaValue) {
        assertEquals(type, field.getType());
        assertEquals(name, field.getName());
        Scalar[] fields = field.getFields();
        assertEquals(exponentOp, fields[0].getOperator());
        assertEquals(exponentValue, fields[0].getDefaultValue());

        assertEquals(mantissaOp, fields[1].getOperator());
        assertEquals(mantissaValue, fields[1].getDefaultValue());
    }

    protected void assertScalarField(FieldSet fieldSet, int fieldIndex, Type type, String name, Operator operator) {
        Scalar field = (Scalar) fieldSet.getField(fieldIndex);
        assertScalarField(field, type, name);
        assertEquals(operator, field.getOperator());
    }

    protected void assertSequenceLengthField(Sequence sequence, String name, Type type, Operator operator) {
        assertEquals(type, sequence.getLength().getType());
        assertEquals(name, sequence.getLength().getName());
        assertEquals(operator, sequence.getLength().getOperator());
    }

    protected void assertSequence(MessageTemplate messageTemplate, int fieldIndex, int fieldCount) {
        Sequence sequence = (Sequence) messageTemplate.getField(fieldIndex);
        assertEquals(fieldCount, sequence.getFieldCount());
    }

    protected void assertGroup(MessageTemplate messageTemplate, int fieldIndex, String name) {
        Group currentGroup = (Group) messageTemplate.getField(fieldIndex);
        assertEquals(name, currentGroup.getName());
    }

    protected void assertOptionalScalarField(FieldSet fieldSet, int fieldIndex, Type type, String name, Operator operator) {
        Scalar field = (Scalar) fieldSet.getField(fieldIndex);
        assertScalarField(field, type, name);
        assertEquals(operator, field.getOperator());
        assertTrue(field.isOptional());
    }

    private void assertScalarField(Scalar field, Type type, String name) {
        assertEquals(name, field.getName());
        assertEquals(type, field.getType());
    }

    protected Document document(String string) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                new InputSource(new ByteArrayInputStream(string.getBytes())));
        return doc;
    }

    protected void assertScalarField(Scalar scalar, Type type, String name, String id, String namespace, String dictionary,
            String key, Operator op, ScalarValue defaultVal, boolean optional) {
        assertScalarField(scalar, type, name, id, namespace, dictionary, key, namespace, op, defaultVal, optional);
    }

    protected void assertScalarField(Scalar scalar, Type type, String name, String id, String namespace, String dictionary,
            String key, String keyNamespace, Operator op, ScalarValue defaultVal, boolean optional) {
        QName qname = new QName(name, namespace);
        assertEquals(type, scalar.getType());
        assertEquals(op, scalar.getOperator());
        assertEquals(qname, scalar.getQName());
        QName keyName = new QName(key, keyNamespace);
        assertEquals(keyName, scalar.getKey());
        assertEquals(id, scalar.getId());
        assertEquals(dictionary, scalar.getDictionary());
        assertEquals(defaultVal, scalar.getDefaultValue());
        assertEquals(optional, scalar.isOptional());
    }

    protected InputStream resource(String url) {
        return this.getClass().getClassLoader().getResourceAsStream(url);
    }

    protected ByteVectorValue bv(String hexString) {
        return byt(byt(hexString));
    }

    protected static void assertEquals(BigDecimal expected, BigDecimal actual) {
        if (expected.compareTo(actual) != 0)
            throw new AssertionFailedError("expected:<" + expected.toPlainString() + "> bug was:<" + actual.toPlainString() + ">");
    }
}
