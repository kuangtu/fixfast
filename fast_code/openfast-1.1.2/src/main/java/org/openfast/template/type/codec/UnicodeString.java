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
/**
 *
 */
package org.openfast.template.type.codec;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.openfast.ByteVectorValue;
import org.openfast.ScalarValue;
import org.openfast.StringValue;
import org.openfast.error.FastConstants;
import org.openfast.error.FastException;

final class UnicodeString extends NotStopBitEncodedTypeCodec {
    private static final long serialVersionUID = 1L;

    UnicodeString() {}

    /**
     * Takes a ScalarValue object, and converts it to a byte array
     * 
     * @param value
     *            The ScalarValue to be encoded
     * @return Returns a byte array of the passed object
     */
    public byte[] encodeValue(ScalarValue value) {
        try {
            byte[] utf8encoding = ((StringValue) value).value.getBytes("UTF8");
            return TypeCodec.BYTE_VECTOR.encode(new ByteVectorValue(utf8encoding));
        } catch (UnsupportedEncodingException e) {
            throw new FastException("Apparently Unicode is no longer supported by Java.", FastConstants.IMPOSSIBLE_EXCEPTION, e);
        }
    }

    /**
     * Reads in a stream of data and stores it to a StringValue object
     * 
     * @param in
     *            The InputStream to be decoded
     * @return Returns a new StringValue object with the data stream as a String
     */
    public ScalarValue decode(InputStream in) {
        ByteVectorValue value = (ByteVectorValue) TypeCodec.BYTE_VECTOR.decode(in);
        try {
            return new StringValue(new String(value.value, "UTF8"));
        } catch (UnsupportedEncodingException e) {
            throw new FastException("Apparently Unicode is no longer supported by Java.", FastConstants.IMPOSSIBLE_EXCEPTION, e);
        }
    }

    /**
     * @return Returns a new StringValue object with the passed value
     */
    public ScalarValue fromString(String value) {
        return new StringValue(value);
    }

    /**
     * 
     * @return Returns a new StringValue with a default value
     */
    public ScalarValue getDefaultValue() {
        return new StringValue("");
    }
}
