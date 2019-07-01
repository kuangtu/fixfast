package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Scalar;
import org.openfast.template.Sequence;
import org.openfast.util.XmlWriter;

public class SequenceSerializer extends AbstractFieldSerializer implements FieldSerializer {
    public boolean canSerialize(Field field) {
        return field instanceof Sequence;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        Sequence sequence = (Sequence) field;
        writer.start("sequence");
        writeCommonAttributes(writer, field, context);
        writeTypeReference(writer, sequence.getGroup(), context);
        writeLength(writer, sequence, context);
        if (!sequence.isImplicitLength()) {
            writer.start("length");
            Scalar length = sequence.getLength();
            writer.addAttribute("name", length.getName());
            if (!length.getQName().getNamespace().equals(context.getNamespace()))
                writer.addAttribute("ns", length.getQName().getNamespace());
            if (length.getId() != null)
                writer.addAttribute("id", length.getId());
            writer.end();
        }
        writeChildren(writer, context, sequence.getGroup());
        writer.end();
    }
}
