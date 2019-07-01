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
import org.openfast.ScalarValue;

import org.openfast.error.FastConstants;

import org.openfast.template.Scalar;
import org.openfast.template.type.Type;


public abstract class OptionallyPresentOperatorCodec extends OperatorCodec {
    private static final long serialVersionUID = 1L;

    protected OptionallyPresentOperatorCodec(Operator operator, Type[] types) {
        super(operator, types);
    }

    /**
     * 
     * @param priorValue
     * @param field
     * @return the value that should be used if no value is present in the fast stream 
     */
    public ScalarValue decodeEmptyValue(ScalarValue priorValue, Scalar field) {
        if (priorValue == ScalarValue.UNDEFINED) {
            return getInitialValue(field);
        }

        return getEmptyValue(priorValue);
    }

    /**
     * @param value
     * @param priorValue
     * @param field
     * @return the value that should be encoded over the fast stream given the previous value for this field
     */
    public ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, Scalar field) {
        if (value != null) {
            return getValueToEncode(value, priorValue, field.getDefaultValue());
        }

        if (field.isOptional()) {
            if (((priorValue == ScalarValue.UNDEFINED) && !field.getDefaultValue().isUndefined()) || ((priorValue != ScalarValue.UNDEFINED) && (priorValue != null))) {
                return ScalarValue.NULL;
            }
        } else {
            Global.handleError(FastConstants.D6_MNDTRY_FIELD_NOT_PRESENT, "The field \"" + field + " is not present.");
        }

        return null;
    }

    protected abstract ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, ScalarValue defaultValue);

    protected abstract ScalarValue getInitialValue(Scalar field);

    protected abstract ScalarValue getEmptyValue(ScalarValue priorValue);
}
