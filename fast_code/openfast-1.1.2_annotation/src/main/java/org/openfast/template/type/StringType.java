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

import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class StringType extends SimpleType {
    private static final long serialVersionUID = 1L;

    public StringType(String typeName, TypeCodec codec, TypeCodec nullableCodec) {
        super(typeName, codec, nullableCodec);
    }

    /**
     * @param value
     * @return StringValue of given value
     */
    public ScalarValue getVal(String value) {
        return new StringValue(value);
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
        if (operator == Operator.DELTA)
            return (optional) ? TypeCodec.NULLABLE_STRING_DELTA : TypeCodec.STRING_DELTA;
        return super.getCodec(operator, optional);
    }

    /**
     * @return Returns a new StringValue object with empty string as the value
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }

    /**
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the passed value is an instance of an integer or
     *         long
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof StringValue;
    }
}