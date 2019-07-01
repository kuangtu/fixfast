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

import org.openfast.DecimalValue;
import org.openfast.Global;
import org.openfast.ScalarValue;
import org.openfast.error.FastConstants;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

final class DecimalType extends SimpleType {
    private static final long serialVersionUID = 1L;

    DecimalType() {
        super("decimal", TypeCodec.SF_SCALED_NUMBER, TypeCodec.NULLABLE_SF_SCALED_NUMBER);
    }

    /**
     * Get the approprivate codec for the passed operator
     * 
     * @param operator
     *            The operator object in which the codec is trying to get
     * @param optional
     *            Determines if the Field is required or not for the data
     * @return Returns the codec if the field is required
     */
    public TypeCodec getCodec(Operator operator, boolean optional) {
        return super.getCodec(operator, optional);
    }

    /**
     * @param value
     * @return
     */
    protected ScalarValue getVal(String value) {
        try {
            return new DecimalValue(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            Global.handleError(FastConstants.S3_INITIAL_VALUE_INCOMP, "The value \"" + value + "\" is not compatible with type "
                    + this);
            return null;
        }
    }

    /**
     * @return Returns a new DecimalValue with a defualt value
     */
    public ScalarValue getDefaultValue() {
        return new DecimalValue(0.0);
    }

    /**
     * Determines if previousValue is of type DecimalValue
     * 
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the previousValue is an instance of DecimalValue,
     *         false otherwise
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof DecimalValue;
    }
}