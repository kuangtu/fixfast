package org.openfast.template.serializer;

import org.openfast.template.Field;
import org.openfast.template.StaticTemplateReference;
import org.openfast.util.XmlWriter;

public class StaticTemplateReferenceSerializer implements FieldSerializer {
    public boolean canSerialize(Field field) {
        return field instanceof StaticTemplateReference;
    }

    public void serialize(XmlWriter writer, Field field, SerializingContext context) {
        StaticTemplateReference ref = (StaticTemplateReference) field;
        writer.start("templateRef");
        AbstractFieldSerializer.writeCommonAttributes(writer, ref.getTemplate(), context);
        writer.end();
    }
}
