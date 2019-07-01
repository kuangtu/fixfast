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

public class CopyOperatorCodec extends OptionallyPresentOperatorCodec {
    private static final long serialVersionUID = 1L;

    protected CopyOperatorCodec() {
        super(Operator.COPY, Type.ALL_TYPES);
    }

    /**
     * Determine which values to encode from the passed ScalarValue objects
     * 
     * @param value
     *            The ScalarValue object to be encoded
     * @param priorValue
     *            The priorValue object to be encoded
     * @param defaultValue
     *            The defaultValue object to be encoded
     * @return Returns null if the priorValue is undefined and if the
     *         ScalarValue object equals the defaultValue object or priorValue
     *         object, otherwise returns the ScalarValue object
     */
    protected ScalarValue getValueToEncode(ScalarValue value, ScalarValue priorValue, ScalarValue defaultValue) {
        if ((priorValue == ScalarValue.UNDEFINED) && value.equals(defaultValue)) {
            return null;
        }
        return (value.equals(priorValue)) ? null : value;
    }

    /**
     * Get the initial value of the passed Scalar object as long as the object
     * is defined
     * 
     * @param field
     *            The Scalar object that the initial value is trying to return
     * @return Returns the default value of the passed Scalar object if the
     *         object is defined, otherwise returns null
     */
    protected ScalarValue getInitialValue(Scalar field) {
        if (!field.getDefaultValue().isUndefined()) {
            return field.getDefaultValue();
        }
        if (field.isOptional()) {
            return null;
        }
        Global.handleError(FastConstants.D5_NO_DEFAULT_VALUE, "No default value for " + field);
        return null;
    }

    /**
     * @return Returns the variable priorValue
     */
    protected ScalarValue getEmptyValue(ScalarValue priorValue) {
        return priorValue;
    }

    /**
     * @return newValue
     */
    public ScalarValue decodeValue(ScalarValue newValue, ScalarValue priorValue, Scalar field) {
        return newValue;
    }

    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }
}
