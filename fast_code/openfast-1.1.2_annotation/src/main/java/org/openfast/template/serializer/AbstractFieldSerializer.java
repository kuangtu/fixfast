package org.openfast.template.serializer;

import java.util.List;
import org.openfast.Node;
import org.openfast.error.FastConstants;
import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.Scalar;
import org.openfast.util.XmlWriter;

public abstract class AbstractFieldSerializer implements FieldSerializer {
    protected static void writeCommonAttributes(XmlWriter writer, Field field, SerializingContext context) {
        writer.addAttribute("name", field.getQName().getName());
        if (!context.getNamespace().equals(field.getQName().getNamespace()))
        writer.addAttribute("ns", field.getQName().getNamespace());
        if (field.getId() != null)
            writer.addAttribute("id", field.getId());
        if (field.isOptional())
            writer.addAttribute("presence", "optional");
    }

    protected static void writeOperator(XmlWriter writer, Scalar scalar, SerializingContext context) {
        writer.start(scalar.getOperator().getName());
        if (!scalar.getDictionary().equals(context.getDictionary())) {
            writer.addAttribute("dictionary", scalar.getDictionary());
        }
        if (!scalar.getKey().equals(scalar.getQName())) {
            writer.addAttribute("key", scalar.getKey().getName());
            if (!context.getNamespace().equals(scalar.getKey().getNamespace()))
                writer.addAttribute("ns", scalar.getKey().getNamespace());
        }
        if (!scalar.getDefaultValue().isUndefined()) {
            writer.addAttribute("value", scalar.getDefaultValue().serialize());
        }
        writer.end();
    }

    protected static void writeChildren(XmlWriter writer, SerializingContext context, Group group) {
        for (int i=0; i<group.getFieldCount(); i++) {
            context.serialize(writer, group.getField(i));
        }
    }

    protected static void writeTypeReference(XmlWriter writer, Group group, SerializingContext context) {
        if (group.getTypeReference() != null) {
            writer.start("typeRef");
            writer.addAttribute("name", group.getTypeReference().getName());
            if (!group.getTypeReference().getNamespace().equals(context.getNamespace()))
                writer.addAttribute("ns", group.getTypeReference().getNamespace());
            writer.end();
        }
    }
    
    protected static void writeLength(XmlWriter writer, Node node, SerializingContext context) {
        List lengthNodes = node.getChildren(FastConstants.LENGTH_FIELD);
        if (!lengthNodes.isEmpty()) {
            Node lengthNode = (Node) lengthNodes.get(0);
            writer.start("length");
            writer.addAttribute("name", lengthNode.getAttribute(FastConstants.LENGTH_NAME_ATTR));
            if (lengthNode.hasAttribute(FastConstants.LENGTH_NS_ATTR)) {
                String namespace = lengthNode.getAttribute(FastConstants.LENGTH_NS_ATTR);
                if (!namespace.equals(context.getNamespace()))
                    writer.addAttribute("ns", namespace);
            }
            if (lengthNode.hasAttribute(FastConstants.LENGTH_ID_ATTR))
                writer.addAttribute("id", lengthNode.getAttribute(FastConstants.LENGTH_ID_ATTR));
            writer.end();
        }
    }
}
