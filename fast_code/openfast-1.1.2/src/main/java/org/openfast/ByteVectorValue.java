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
package org.openfast;

public class ByteVectorValue extends ScalarValue {
    private static final long serialVersionUID = 1L;
    public static final ScalarValue EMPTY_BYTES = new ByteVectorValue(new byte[] {});
    public final byte[] value;
    private int offset;
    private int length;

    public ByteVectorValue(byte[] value) {
        this(value, 0, value.length);
    }

    public ByteVectorValue(byte[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.length = length;
    }

    public byte[] getBytes() {
        return value;
    }
    
    public String serialize() {
        StringBuffer builder = new StringBuffer(value.length * 2);
        for (int i = 0; i < value.length; i++) {
            String hex = Integer.toHexString(value[i] & 0xff);
            if (hex.length() == 1)
                builder.append('0');
            builder.append(hex);
        }
        return builder.toString();
    }

    public String toString() {
        return new String(value, offset, length);
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof ByteVectorValue)) {
            return false;
        }
        return equals((ByteVectorValue) obj);
    }

    public boolean equals(ByteVectorValue other) {
        if (this.length != other.length) {
            return false;
        }
        for (int i = 0; i < length; i++)
            if (this.value[offset + i] != other.value[other.offset + i]) {
                return false;
            }
        return true;
    }

    public int hashCode() {
        return value.hashCode();
    }
}
