package org.openfast.template.serializer;

import org.openfast.Dictionary;
import org.openfast.template.Field;
import org.openfast.util.XmlWriter;

public class SerializingContext {
    private final SerializingContext parent;
    private String currentNamespace = "";
    private String currentTemplateNamespace = "";
    private String dictionary = Dictionary.GLOBAL;

    public SerializingContext(SerializingContext parent) {
        this.parent = parent;
    }
    
    public static SerializingContext createInitialContext(final SerializerRegistry registry) {
        return new SerializingContext(null) {
            public SerializerRegistry getSerializerRegistry() {
                return registry;
            }
        };
    }
    
    public void serialize(XmlWriter writer, Field field) {
        getSerializerRegistry().getSerializer(field).serialize(writer, field, this);
    }

    public SerializerRegistry getSerializerRegistry() {
        return parent.getSerializerRegistry();
    }

    public String getNamespace() {
        return currentNamespace;
    }

    public String getTemplateNamespace() {
        return currentTemplateNamespace;
    }

    public String getDictionary() {
        return dictionary;
    }

    public void setTemplateNamespace(String templateNamespace) {
        currentTemplateNamespace = templateNamespace;
    }

    public void setNamespace(String namespace) {
        currentNamespace = namespace;
    }
}
