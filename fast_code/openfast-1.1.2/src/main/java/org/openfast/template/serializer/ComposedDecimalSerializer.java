package org.openfast.template.serializer;

import org.openfast.template.ComposedScalar;
import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.XmlWriter;

public class ComposedDecimalSerializer implements FieldSerializer {
    public boolean canSerialize(Field field) {
        return field instanceof ComposedScalar && Type.DECIMAL.equals(((ComposedScalar) field).getType());
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        ComposedScalar decimal = (ComposedScalar) field;
        Scalar exponent = decimal.getFields()[0];
        Scalar mantissa = decimal.getFields()[1];
        writer.start("decimal");
        AbstractFieldSerializer.writeCommonAttributes(writer, decimal, context);
        if (!Operator.NONE.equals(exponent.getOperator())) {
            writer.start("exponent");
            AbstractFieldSerializer.writeOperator(writer, exponent, context);
            writer.end();
        }
        if (!Operator.NONE.equals(mantissa.getOperator())) {
            writer.start("mantissa");
            AbstractFieldSerializer.writeOperator(writer, mantissa, context);
            writer.end();
        }
        writer.end();
    }
}
