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

import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class IncrementIntegerOperatorCodec extends OperatorCodec {
    private static final long serialVersionUID = 1L;

    IncrementIntegerOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }
    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
        if (priorValue == null) {
            return value;
        }
        if (value == null) {
            if (field.isOptional()) {
                if (priorValue == ScalarValue.UNDEFINED && field.getDefaultValue().isUndefined()) {
                    return null;
                }
                return ScalarValue.NULL;
            } else {
                throw new IllegalArgumentException();
            }
        }
        if (priorValue.isUndefined()) {
            if (value.equals(field.getDefaultValue())) {
                return null;
            } else {
                return value;
            }
        }
        if (!value.equals(((NumericValue) priorValue).increment())) {
            return value;
        }
        return null;
    }
    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
        return newValue;
    }
    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        if (previousValue == null)
            return null;
        if (previousValue.isUndefined()) {
            if (field.getDefaultValue().isUndefined()) {
                if (field.isOptional()) {
                    return null;
                } else {
                    throw new IllegalStateException("Field with operator increment must send a value if no previous value existed.");
                }
            } else {
                return field.getDefaultValue();
            }
        }
        return ((NumericValue) previousValue).increment();
    }
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}