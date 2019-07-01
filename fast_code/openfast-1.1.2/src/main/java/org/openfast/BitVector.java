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

public class BitVector {
    private static final int VALUE_BITS_SET = 0x7F;
    private static final int STOP_BIT = 0x80;
    private byte[] bytes;
    private int size;

    public BitVector(int size) {
        this(new byte[((size - 1) / 7) + 1]);
    }

    public BitVector(byte[] bytes) {
        this.bytes = bytes;
        this.size = bytes.length * 7;
        bytes[bytes.length - 1] |= STOP_BIT;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getTruncatedBytes() {
        int index = bytes.length - 1;
        for (; (index > 0) && ((bytes[index] & VALUE_BITS_SET) == 0); index--)
            ;
        if (index == (bytes.length - 1)) {
            return bytes;
        }
        byte[] truncated = new byte[index + 1];
        System.arraycopy(bytes, 0, truncated, 0, index + 1);
        truncated[truncated.length - 1] |= STOP_BIT;
        return truncated;
    }

    public int getSize() {
        return this.size;
    }

    public void set(int fieldIndex) {
        bytes[fieldIndex / 7] |= (1 << (6 - (fieldIndex % 7)));
    }

    public boolean isSet(int fieldIndex) {
        if (fieldIndex >= bytes.length * 7)
            return false;
        return ((bytes[fieldIndex / 7] & (1 << (6 - (fieldIndex % 7)))) > 0);
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof BitVector)) {
            return false;
        }
        return equals((BitVector) obj);
    }

    public boolean equals(BitVector other) {
        if (other.size != this.size) {
            return false;
        }
        for (int i = 0; i < this.bytes.length; i++) {
            if (this.bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return bytes.hashCode();
    }

    public boolean isOverlong() {
        return (bytes.length > 1) && ((bytes[bytes.length - 1] & VALUE_BITS_SET) == 0);
    }

    public String toString() {
        return "BitVector [" + ByteUtil.convertByteArrayToBitString(bytes) + "]";
    }

    public int indexOfLastSet() {
        int index = bytes.length * 7 - 1;
        while (index >= 0 && !isSet(index))
            index--;
        return index;
    }
}
