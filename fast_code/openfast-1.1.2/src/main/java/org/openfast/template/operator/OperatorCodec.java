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
package org.openfast.template.operator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.openfast.BitVectorBuilder;
import org.openfast.FieldValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;
import org.openfast.util.Key;

public abstract class OperatorCodec implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Map OPERATOR_MAP = new HashMap();
    protected static final OperatorCodec NONE_ALL = new NoneOperatorCodec(Operator.NONE, Type.ALL_TYPES);
    protected static final OperatorCodec CONSTANT_ALL = new ConstantOperatorCodec(Operator.CONSTANT, Type.ALL_TYPES);
    protected static final OperatorCodec DEFAULT_ALL = new DefaultOperatorCodec(Operator.DEFAULT, Type.ALL_TYPES);
    protected static final OperatorCodec COPY_ALL = new CopyOperatorCodec();
    protected static final OperatorCodec INCREMENT_INTEGER = new IncrementIntegerOperatorCodec(Operator.INCREMENT, Type.INTEGER_TYPES);
    protected static final OperatorCodec DELTA_INTEGER = new DeltaIntegerOperatorCodec(Operator.DELTA, Type.INTEGER_TYPES);
    protected static final OperatorCodec DELTA_STRING = new DeltaStringOperatorCodec();
    protected static final OperatorCodec DELTA_DECIMAL = new DeltaDecimalOperatorCodec();
    protected static final OperatorCodec TAIL = new TailOperatorCodec(Operator.TAIL, new Type[] { Type.ASCII, Type.STRING,
            Type.UNICODE, Type.BYTE_VECTOR });
    private final Operator operator;

    /**
     * 
     * @param operator
     *            The name of the Operator as a string
     * @param types
     *            The type array to be stored in the keys
     */
    protected OperatorCodec(Operator operator, Type[] types) {
        this.operator = operator;
        for (int i = 0; i < types.length; i++) {
            Key key = new Key(operator, types[i]);
            if (!OPERATOR_MAP.containsKey(key)) {
                OPERATOR_MAP.put(key, this);
            }
        }
    }

    /**
     * Find the operator by the key
     * 
     * @param operator
     *            the fast operator
     * @param type
     *            The type of the operator, stored to the key
     * @return Returns the operator object with the specified key
     */
    public static OperatorCodec getCodec(Operator operator, Type type) {
        Key key = new Key(operator, type);
        if (!OPERATOR_MAP.containsKey(key)) {
            Global.handleError(FastConstants.S2_OPERATOR_TYPE_INCOMP, "The operator \"" + operator
                    + "\" is not compatible with type \"" + type + "\"");
            throw new IllegalArgumentException();
        }
        return (OperatorCodec) OPERATOR_MAP.get(key);
    }

    public abstract ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field);

    public abstract ScalarValue decodeValue(ScalarValue newValue, ScalarValue priorValue, Scalar field);

    /**
     * 
     * @param encoding
     *            The byte array that is being encoded
     * @param fieldValue
     *            The fieldValue object to check
     * @return Returns true if the byte array has a length larger then zero
     */
    public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
        return encoding.length != 0;
    }

    public abstract ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field);

    /**
     * Use this to show that there is a MapBit present
     * 
     * @param optional
     *            The Optional boolean
     * @return Returns true
     */
    public boolean usesPresenceMapBit(boolean optional) {
        return true;
    }

    /**
     * 
     * @param value
     * @param priorValue
     * @param scalar
     * @param presenceMapBuilder
     * @return the value that should be encoded given the previous value
     */
    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar scalar, BitVectorBuilder presenceMapBuilder) {
        ScalarValue valueToEncode = getValueToEncode(value, priorValue, scalar);
        if (valueToEncode == null)
            presenceMapBuilder.skip();
        else
            presenceMapBuilder.set();
        return valueToEncode;
    }

    public Operator getOperator() {
        return operator;
    }

    public boolean canEncode(ScalarValue value, Scalar field) {
        return true;
    }

    public boolean shouldDecodeType() {
        return true;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }

    public String toString() {
        return operator.toString();
    }
}
