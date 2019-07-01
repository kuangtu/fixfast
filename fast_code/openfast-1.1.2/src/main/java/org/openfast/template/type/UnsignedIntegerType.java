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

import org.openfast.template.operator.Operator;
import org.openfast.template.type.codec.TypeCodec;

public class UnsignedIntegerType extends IntegerType {
    private static final long serialVersionUID = 1L;

    public UnsignedIntegerType(int numberBits, long maxValue) {
        super("uInt" + numberBits, 0, maxValue, TypeCodec.UINT, TypeCodec.NULLABLE_UNSIGNED_INTEGER);
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
        if (operator.equals(Operator.DELTA))
            if (optional)
                return TypeCodec.NULLABLE_INTEGER;
            else
                return TypeCodec.INTEGER;
        return super.getCodec(operator, optional);
    }
}
