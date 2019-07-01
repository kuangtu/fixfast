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
package org.openfast.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.QName;
import org.openfast.template.type.Type;

public class ComposedScalar extends Field {
    private static final long serialVersionUID = 1L;
    private static final Class ScalarValueType = null;
    private Scalar[] fields;
    private ComposedValueConverter valueConverter;
    private Type type;
    private FieldValue[] values;

    public ComposedScalar(String name, Type type, Scalar[] fields, boolean optional, ComposedValueConverter valueConverter) {
        this(new QName(name), type, fields, optional, valueConverter);
    }

    public ComposedScalar(QName name, Type type, Scalar[] fields, boolean optional, ComposedValueConverter valueConverter) {
        super(name, optional);
        this.fields = fields;
        this.valueConverter = valueConverter;
        this.type = type;
        this.values = new FieldValue[fields.length];
    }

    public FieldValue createValue(String value) {
        return type.getValue(value);
    }

    public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader presenceMapReader) {
        synchronized(values) {
            Arrays.fill(values, null);
            for (int i = 0; i < fields.length; i++) {
                values[i] = fields[i].decode(in, template, context, presenceMapReader);
                if (i == 0 && values[0] == null)
                    return null;
            }
            return valueConverter.compose(values);
        }
    }

    public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
        if (value == null) {
            // Only encode null in the first field.
            return fields[0].encode(null, template, context, presenceMapBuilder);
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream(fields.length * 8);
            FieldValue[] values = valueConverter.split(value);
            for (int i = 0; i < fields.length; i++) {
                try {
                    buffer.write(fields[i].encode(values[i], template, context, presenceMapBuilder));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return buffer.toByteArray();
        }
    }

    public String getTypeName() {
        return type.getName();
    }

    public Class getValueType() {
        return ScalarValueType;
    }

    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return false;
    }

    public boolean usesPresenceMapBit() {
        for (int i=0; i<fields.length; i++) {
            if (fields[i].usesPresenceMapBit())
                return true;
        }
        return false;
    }

    public Type getType() {
        return type;
    }

    public Scalar[] getFields() {
        return fields;
    }

    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !obj.getClass().equals(ComposedScalar.class))
            return false;
        ComposedScalar other = (ComposedScalar) obj;
        if (other.fields.length != fields.length)
            return false;
        if (!other.getName().equals(getName()))
            return false;
        for (int i = 0; i < fields.length; i++) {
            if (!other.fields[i].getType().equals(fields[i].getType()))
                return false;
            if (!other.fields[i].getTypeCodec().equals(fields[i].getTypeCodec()))
                return false;
            if (!other.fields[i].getOperator().equals(fields[i].getOperator()))
                return false;
            if (!other.fields[i].getOperatorCodec().equals(fields[i].getOperatorCodec()))
                return false;
            if (!other.fields[i].getDefaultValue().equals(fields[i].getDefaultValue()))
                return false;
            if (!other.fields[i].getDictionary().equals(fields[i].getDictionary()))
                return false;
        }
        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Composed {");
        for (int i = 0; i < fields.length; i++)
            builder.append(fields[i].toString()).append(", ");
        builder.delete(builder.length() - 2, builder.length());
        return builder.append("}").toString();
    }
}
