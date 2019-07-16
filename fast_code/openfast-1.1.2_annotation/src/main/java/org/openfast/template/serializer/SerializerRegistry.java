package org.openfast.template.serializer;

import java.util.ArrayList;
import java.util.List;
import org.openfast.template.Field;

public class SerializerRegistry {
    private List serializers = new ArrayList();

    public void addFieldSerializer(FieldSerializer serializer) {
        serializers.add(serializer);
    }

    public FieldSerializer getSerializer(Field field) {
        for (int i=serializers.size()-1; i>=0; i--) {
            FieldSerializer serializer = (FieldSerializer) serializers.get(i);
            if (serializer.canSerialize(field)) {
                return serializer;
            }
        }
        return null;
    }
}
