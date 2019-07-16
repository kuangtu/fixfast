package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.util.XmlWriter;

public interface FieldSerializer {
    void serialize(XmlWriter writer, Field field, SerializingContext context);

    boolean canSerialize(Field field);
}
