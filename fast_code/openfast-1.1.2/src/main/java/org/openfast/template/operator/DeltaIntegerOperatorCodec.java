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

import org.openfast.Global;
import org.openfast.NumericValue;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.Scalar;
import org.openfast.template.type.Type;

final class DeltaIntegerOperatorCodec extends AlwaysPresentOperatorCodec {
    private static final long serialVersionUID = 1L;

    DeltaIntegerOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }

    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
        if (priorValue == null) {
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " must have a priorValue defined.");
            return null;
        }

        if (value == null) {
            if (field.isOptional()) {
                return ScalarValue.NULL;
            } else {
                throw new IllegalArgumentException("Mandatory fields can't be null.");
            }
        }

        if (priorValue.isUndefined()) {
            priorValue = field.getBaseValue();
        }

        return ((NumericValue) value).subtract((NumericValue) priorValue);
    }

    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue previousValue, Scalar field) {
        if (previousValue == null) {
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field " + field + " must have a priorValue defined.");
            return null;
        }

        if ((newValue == null) || newValue.isNull()) {
            return null;
        }

        if (previousValue.isUndefined()) {
            if (field.getDefaultValue().isUndefined()) {
                previousValue = field.getBaseValue();
            } else {
                previousValue = field.getDefaultValue();
            }
        }

        return ((NumericValue) newValue).add((NumericValue) previousValue);
    }

    public ScalarValue decodeEmptyValue(ScalarValue previousValue, Scalar field) {
        if (previousValue.isUndefined()) {
            if (field.getDefaultValue().isUndefined()) {
                if (field.isOptional()) {
                    return ScalarValue.UNDEFINED;
                } else {
                    Global.handleError(FastConstants.D5_NO_DEFAULT_VALUE, "");
                }
            } else {
                return field.getDefaultValue();
            }
        }

        return previousValue;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}