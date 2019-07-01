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
package org.openfast.template.type;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;
import org.openfast.util.Util;

public abstract class Type implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Map TYPE_NAME_MAP = new LinkedHashMap();
    private final String name;

    public Type(String typeName) {
        this.name = typeName;
        TYPE_NAME_MAP.put(typeName, this);
    }
    /**
     * Return the type that is being searched for
     * 
     * @param typeName
     *            The type name that being searched for
     * @return Return a Type object of the type that is being searched for
     */
    public static Type getType(String typeName) {
        if (!TYPE_NAME_MAP.containsKey(typeName))
            throw new IllegalArgumentException("The type named " + typeName + " does not exist.  Existing types are "
                    + Util.collectionToString(TYPE_NAME_MAP.keySet()));
        return (Type) TYPE_NAME_MAP.get(typeName);
    }
    /**
     * 
     * @return Returns name as a string
     */
    public String getName() {
        return name;
    }
    /**
     * @return Returns the name as a string
     */
    public String toString() {
        return name;
    }
    public String serialize(ScalarValue value) {
        return value.toString();
    }
    public abstract TypeCodec getCodec(Operator operator, boolean optional);
    public abstract ScalarValue getValue(String value);
    public abstract ScalarValue getDefaultValue();
    public abstract boolean isValueOf(ScalarValue previousValue);
    public void validateValue(ScalarValue value) {}

    public final static Type U8 = new UnsignedIntegerType(8, 256);
    public final static Type U16 = new UnsignedIntegerType(16, 65536);
    public final static Type U32 = new UnsignedIntegerType(32, 4294967295L);
    public final static Type U64 = new UnsignedIntegerType(64, Long.MAX_VALUE);
    public final static Type I8 = new SignedIntegerType(8, Byte.MIN_VALUE, Byte.MAX_VALUE);
    public final static Type I16 = new SignedIntegerType(16, Short.MIN_VALUE, Short.MAX_VALUE);
    public final static Type I32 = new SignedIntegerType(32, Integer.MIN_VALUE, Integer.MAX_VALUE);
    public final static Type I64 = new SignedIntegerType(64, Long.MIN_VALUE, Long.MAX_VALUE);
    public final static Type STRING = new StringType("string", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII) {
        private static final long serialVersionUID = 1L;

        public ScalarValue getValue(byte[] bytes) {
            return new StringValue(new String(bytes));
        }
        public ScalarValue getValue(byte[] bytes, int offset, int length) {
            return new StringValue(new String(bytes, offset, length));
        }
    };
    public final static Type ASCII = new StringType("ascii", TypeCodec.ASCII, TypeCodec.NULLABLE_ASCII) {
        private static final long serialVersionUID = 1L;

        public ScalarValue getValue(byte[] bytes) {
            return new StringValue(new String(bytes));
        }
        public ScalarValue getValue(byte[] bytes, int offset, int length) {
            return new StringValue(new String(bytes, offset, length));
        }
    };
    public final static Type UNICODE = new StringType("unicode", TypeCodec.UNICODE, TypeCodec.NULLABLE_UNICODE) {
        private static final long serialVersionUID = 1L;

        public ScalarValue getValue(byte[] bytes) {
            try {
                return new StringValue(new String(bytes, "UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        public ScalarValue getValue(byte[] bytes, int offset, int length) {
            try {
                return new StringValue(new String(bytes, offset, length, "UTF8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    };
    public final static Type BYTE_VECTOR = new ByteVectorType();
    public final static Type DECIMAL = new DecimalType();
    public static final Type[] ALL_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64, STRING, ASCII, UNICODE, BYTE_VECTOR,
            DECIMAL };
    public static final Type[] INTEGER_TYPES = new Type[] { U8, U16, U32, U64, I8, I16, I32, I64 };

    public static Map getRegisteredTypeMap() {
        return TYPE_NAME_MAP;
    }
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        return obj.getClass().equals(this.getClass());
    }
    public int hashCode() {
        return name.hashCode();
    }
    public ScalarValue getValue(byte[] bytes) {
        throw new UnsupportedOperationException();
    }
    public ScalarValue getValue(byte[] bytes, int offset, int length) {
        throw new UnsupportedOperationException();
    }
}
