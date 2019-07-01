package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.StringType;
import org.openfast.util.XmlWriter;

public class ScalarSerializer extends AbstractFieldSerializer implements FieldSerializer {

    public boolean canSerialize(Field field) {
        return field instanceof Scalar;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        Scalar scalar = writeStart(writer, field, context);
        writeEnd(writer, context, scalar);
    }

    protected void writeEnd(XmlWriter writer, SerializingContext context, Scalar scalar) {
        if (!Operator.NONE.equals(scalar.getOperator())) {
            writeOperator(writer, scalar, context);
        }
        writer.end();
    }

    protected Scalar writeStart(XmlWriter writer, Field field, SerializingContext context) {
        Scalar scalar = (Scalar) field;
        String nodeName = getNodeName(scalar);
        writer.start(nodeName);
        writeCommonAttributes(writer, field, context);
        return scalar;
    }

    private String getNodeName(Scalar scalar) {
        String nodeName = scalar.getType().getName();
        if (scalar.getType() instanceof StringType)
            nodeName = "string";
        return nodeName;
    }
}
