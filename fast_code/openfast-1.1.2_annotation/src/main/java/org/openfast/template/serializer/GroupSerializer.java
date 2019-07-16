package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.util.XmlWriter;

public class GroupSerializer extends AbstractFieldSerializer implements FieldSerializer {
    public boolean canSerialize(Field field) {
        return field instanceof Group;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        Group group = (Group) field;
        writer.start("group");
        writeCommonAttributes(writer, field, context);
        writeTypeReference(writer, group, context);
        writeChildren(writer, context, group);
        writer.end();
    }
}
