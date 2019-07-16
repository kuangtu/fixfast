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

import org.openfast.ByteUtil;
import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.template.type.codec.TypeCodec;

final class ByteVectorType extends SimpleType {
    private static final long serialVersionUID = 1L;

    ByteVectorType() {
        super("byteVector", TypeCodec.BYTE_VECTOR, TypeCodec.NULLABLE_BYTE_VECTOR_TYPE);
    }

    /**
     * @param value
     * @return
     */
    protected ScalarValue getVal(String value) {
        return new ByteVectorValue(ByteUtil.convertHexStringToByteArray(value));
    }

    /**
     * @return Returns the default value
     */
    public ScalarValue getDefaultValue() {
        return new ByteVectorValue(new byte[] {});
    }

    /**
     * Determines if previousValue is of type ByteVectorValue
     * 
     * @param previousValue
     *            The previous value of the Field, used in determining the
     *            corresponding field value for the current message being
     *            decoded.
     * @return Returns true if the previousValue is an instance of
     *         ByteVectorValue, false otherwise
     */
    public boolean isValueOf(ScalarValue previousValue) {
        return previousValue instanceof ByteVectorValue;
    }
    
    public ScalarValue getValue(byte[] bytes) {
        return new ByteVectorValue(bytes);
    }
    public ScalarValue getValue(byte[] bytes, int offset, int length) {
        return new ByteVectorValue(bytes, offset, length);
    }
}