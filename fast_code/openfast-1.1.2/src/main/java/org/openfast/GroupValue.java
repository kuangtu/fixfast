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
package org.openfast;

import java.math.BigDecimal;
import java.util.Iterator;

import org.openfast.template.Field;
import org.openfast.template.Group;
import org.openfast.template.LongValue;
import org.openfast.template.Scalar;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.Type;
import org.openfast.util.ArrayIterator;

public class GroupValue implements FieldValue {
    private static final long serialVersionUID = 1L;

    protected final FieldValue[] values;

    private final Group group;

    public GroupValue(Group group, FieldValue[] values) {
        if (group == null) {
            throw new NullPointerException();
        }

        this.group = group;
        this.values = values;

        for (int i=0; i<group.getFieldCount(); i++) {
            if (group.getField(i) instanceof Scalar) {
                Scalar scalar = ((Scalar) group.getField(i));
                if (scalar.getOperator().equals(Operator.CONSTANT) && !scalar.isOptional()) {
                    values[i] = scalar.getDefaultValue();
                }
            }
        }
    }

    public GroupValue(Group group) {
        this(group, new FieldValue[group.getFieldCount()]);
    }

    public Iterator iterator() {
        return new ArrayIterator(values);
    }

    public int getInt(int fieldIndex) {
        return getScalar(fieldIndex).toInt();
    }

    public int getInt(String fieldName) {
        // BAD ABSTRACTION
        if (!group.hasField(fieldName)) {
            if (group.hasIntrospectiveField(fieldName)) {
                Scalar scalar = group.getIntrospectiveField(fieldName);
                if (scalar.getType().equals(Type.UNICODE) || scalar.getType().equals(Type.STRING)
                        || scalar.getType().equals(Type.ASCII))
                    return getString(scalar.getName()).length();
                if (scalar.getType().equals(Type.BYTE_VECTOR))
                    return getBytes(scalar.getName()).length;
            }

        }
        return getScalar(fieldName).toInt();
    }

    public boolean getBool(String fieldName) {
        if (!isDefined(fieldName))
            return false;
        return getScalar(fieldName).toInt() != 0;
    }

    public long getLong(int fieldIndex) {
        return getScalar(fieldIndex).toLong();
    }

    public long getLong(String fieldName) {
        return getScalar(fieldName).toLong();
    }

    public byte getByte(int fieldIndex) {
        return getScalar(fieldIndex).toByte();
    }

    public byte getByte(String fieldName) {
        return getScalar(fieldName).toByte();
    }

    public short getShort(int fieldIndex) {
        return getScalar(fieldIndex).toShort();
    }

    public short getShort(String fieldName) {
        return getScalar(fieldName).toShort();
    }

    public String getString(int index) {
        return getValue(index).toString();
    }

    public String getString(String fieldName) {
        FieldValue value = getValue(fieldName);
        return (value == null) ? null : value.toString();
    }

    public double getDouble(int fieldIndex) {
        return getScalar(fieldIndex).toDouble();
    }

    public double getDouble(String fieldName) {
        return getScalar(fieldName).toDouble();
    }

    public BigDecimal getBigDecimal(int fieldIndex) {
        return getScalar(fieldIndex).toBigDecimal();
    }

    public BigDecimal getBigDecimal(String fieldName) {
        return getScalar(fieldName).toBigDecimal();
    }

    public byte[] getBytes(int fieldIndex) {
        return getScalar(fieldIndex).getBytes();
    }

    public byte[] getBytes(String fieldName) {
        return getScalar(fieldName).getBytes();
    }

    public SequenceValue getSequence(int fieldIndex) {
        return (SequenceValue) getValue(fieldIndex);
    }

    public SequenceValue getSequence(String fieldName) {
        return (SequenceValue) getValue(fieldName);
    }

    public ScalarValue getScalar(int fieldIndex) {
        return (ScalarValue) getValue(fieldIndex);
    }

    public ScalarValue getScalar(String fieldName) {
        return (ScalarValue) getValue(fieldName);
    }

    public GroupValue getGroup(int fieldIndex) {
        return (GroupValue) getValue(fieldIndex);
    }

    public GroupValue getGroup(String fieldName) {
        return (GroupValue) getValue(fieldName);
    }

    public FieldValue getValue(int fieldIndex) {
        return values[fieldIndex];
    }

    public FieldValue getValue(String fieldName) {
        if (!group.hasField(fieldName)) {
            return null;
        }
        return values[group.getFieldIndex(fieldName)];
    }

    public Group getGroup() {
        return group;
    }

    public void setString(Field field, String value) {
        if (field == null)
            throw new IllegalArgumentException("Field must not be null [value=" + value + "]");
        setFieldValue(field, field.createValue(value));
    }

    public void setFieldValue(Field field, FieldValue value) {
        setFieldValue(group.getFieldIndex(field), value);
    }

    public void setFieldValue(int fieldIndex, FieldValue value) {
        values[fieldIndex] = value;
    }

    public void setBitVector(int fieldIndex, BitVector vector) {
        values[fieldIndex] = new BitVectorValue(vector);
    }

    public void setByteVector(int fieldIndex, byte[] bytes) {
        values[fieldIndex] = new ByteVectorValue(bytes);
    }

    public void setByteVector(String fieldName, byte[] bytes) {
        setFieldValue(fieldName, new ByteVectorValue(bytes));
    }

    public void setDecimal(int fieldIndex, double value) {
        values[fieldIndex] = new DecimalValue(value);
    }

    public void setDecimal(String fieldName, double value) {
        setFieldValue(fieldName, new DecimalValue(value));
    }

    public void setDecimal(int fieldIndex, BigDecimal value) {
        values[fieldIndex] = new DecimalValue(value);
    }

    public void setDecimal(String fieldName, BigDecimal value) {
        setFieldValue(fieldName, new DecimalValue(value));
    }

    public void setInteger(String fieldName, int value) {
        setFieldValue(fieldName, new IntegerValue(value));
    }

    public void setInteger(int fieldIndex, int value) {
        values[fieldIndex] = new IntegerValue(value);
    }

    public void setBool(String fieldName, boolean value) {
        setFieldValue(fieldName, new IntegerValue(value ? 1 : 0));
    }
    
    public void setLong(String fieldName, long value) {
        setFieldValue(fieldName, new LongValue(value));
    }

    public void setLong(int fieldIndex, long value) {
        values[fieldIndex] = new LongValue(value);
    }

    public void setString(int fieldIndex, String value) {
        values[fieldIndex] = group.getField(fieldIndex).createValue(value);
    }

    public void setString(String fieldName, String value) {
        setFieldValue(fieldName, group.getField(fieldName).createValue(value));
    }


    public void setFieldValue(int fieldIndex, Object value) {
        FieldValue fieldValue = ScalarValue.NULL;
        if (value instanceof String) {
            fieldValue = new StringValue(String.valueOf(value));
        } else if (value instanceof Integer) {
            fieldValue = new IntegerValue(((Integer) value).intValue());
        } else if (value instanceof Long) {
            fieldValue = new LongValue(((Long) value).longValue());
        } else if (value instanceof Boolean) {
            fieldValue = new IntegerValue(((Boolean) value).booleanValue() ? 1 : 0);
        } else if (value instanceof Double) {
            fieldValue = new DecimalValue(((Double) value).doubleValue());
        }
        setFieldValue(fieldIndex, fieldValue);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if ((other == null) || !(other instanceof GroupValue)) {
            return false;
        }

        return equals((GroupValue) other);
    }

    private boolean equals(GroupValue other) {
        if (values.length != other.values.length) {
            return false;
        }

        for (int i = 0; i < values.length; i++) {
            if (values[i] == null) {
                if (other.values[i] != null)
                    return false;
            } else if (!values[i].equals(other.values[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(group).append(" -> {");
        for (int i = 0; i < values.length; i++) {
            builder.append(values[i]).append(", ");
        }

        if (values.length > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        builder.append("}");
        return builder.toString();
    }

    public void setFieldValue(String fieldName, FieldValue value) {
        if (!group.hasField(fieldName))
            throw new IllegalArgumentException("The field " + fieldName + " does not exist in group " + group);
        int index = group.getFieldIndex(fieldName);
        setFieldValue(index, value);
    }

    public int getFieldCount() {
        return values.length;
    }

    public void setFieldValue(String fieldName, String value) {
        setFieldValue(fieldName, group.getField(fieldName).createValue(value));
    }

    public boolean isDefined(int fieldIndex) {
        return fieldIndex < values.length && fieldIndex >= 0 && values[fieldIndex] != null;
    }

    public boolean isDefined(String fieldName) {
        return getValue(fieldName) != null;
    }

    public FieldValue copy() {
        FieldValue[] copies = new FieldValue[values.length];
        for (int i = 0; i < copies.length; i++) {
            copies[i] = values[i].copy();
        }
        return new GroupValue(group, this.values);
    }
}
